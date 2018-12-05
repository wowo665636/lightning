package com.li.util;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;
import com.li.redis.Constant;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * sql parser tool 工具类
 * @param <T>
 */
public class  SqlParserTool<T> {

    private String sql;
    private T log;
    private  Map<String, Object> tableMap ;

    public  SqlParserTool(String sql,T log){
        this.sql = sql;
        this.log = log;
    }

    /**
     * 解析redis 配置sql 规则
     *
     * @return
     */
    public Map<String, Object> parserSql() {

        if (StringUtils.isBlank(sql)) {
            System.err.println("parserSql error,message=计算规则尚未配置");
            return null;
        }
        String dbType = JdbcConstants.MYSQL;
        try {
            Map<String, Object> map = new HashMap<>();
            String result = SQLUtils.format(sql, dbType);
            List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
            SQLStatement stmt = stmtList.get(0);
            MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
            stmt.accept(visitor);
            //map.put(Constant.TABLE, visitor.getCurrentTable());//添加表名称
            map.put(Constant.FIELDS, visitor.getColumns());// 统计指标
            map.put(Constant.WHERE, visitor.getConditions());// 过滤条件
            map.put(Constant.GROUP, visitor.getGroupByColumns());// 维度
          /*  System.out.println("SQL : " + result);
            //获取表名称
            System.out.println("Tables : " + visitor.getCurrentTable());
            //获取操作方法名称,依赖于表名称
            System.out.println("Manipulation : " + visitor.getTables());
            //获取字段名称
            System.out.println("fields : " + visitor.getColumns());
            System.out.println("where : " + visitor.getConditions());
            //获取字段名称
            System.out.println("group : " +  visitor.getGroupByColumns());*/
            tableMap = map;
            return map;

        } catch (Exception e) {
            System.err.println("parserSql exception,message=配置sql语法错误");
        }
        return null;

    }

    /**
     * 验证是否满足where 条件
     *
     * @return
     */
    public boolean checkWhere() {
        try {
            if (tableMap.get(Constant.WHERE) == null) {
                return true;
            }
            List<TableStat.Condition> conditions = (List<TableStat.Condition>) tableMap.get(Constant.WHERE);
            if (CollectionUtils.isEmpty(conditions)) {
                return true;
            }
            for (int i = 0; i < conditions.size(); i++) {
                TableStat.Condition condition = conditions.get(i);
                String column = condition.getColumn().getName();//限制条件
                String operator = condition.getOperator();// 校验逻辑
                List<Object> restrictions = condition.getValues();//限制条件值

                if (CollectionUtils.isEmpty(restrictions)) {
                    continue;
                }
                try {
                    Class classz = log.getClass();
                    Field field = classz.getDeclaredField(column);
                    field.setAccessible(true);
                    String field_key = field.getName();
                    Object obj = field.get(log);// 获取日志字段值
                    String field_value = obj == null ? "" :obj.toString();
                    int flag = 0;// 是否满足限制条件,默认不满足条件
                    for (Object restriction : restrictions) {
                        //判定 sql operator
                        switch (operator.trim().toUpperCase()) {
                            case Constant.BI_SQL_UNEQUAL:
                                //!=
                                if (field_value.equals(restriction.toString())) {
                                    flag++;
                                }
                                break;
                            case Constant.BI_SQL_NOT_IN:
                                //not in
                                if (field_value.equals(restriction.toString())) {
                                    flag++;// 如果是IN 函数,有一个匹配上就可以
                                }
                                break;
                            default:
                                if (field_value.equals(restriction.toString())) {
                                    flag++;// 如果是IN 函数,有一个匹配上就可以
                                }
                        }

                    }

                    switch (operator.trim().toUpperCase()) {
                        case Constant.BI_SQL_UNEQUAL:
                            if (flag > 0) {
                                //此条记录不满足限制条件
                                //System.err.println("operator:"+operator+","+field_key+"="+field_value+"不满足条件");
                                return false;
                            }
                            break;
                        case Constant.BI_SQL_NOT_IN:
                            if (flag > 0) {
                                //此条记录不满足限制条件
                                // System.err.println("operator:"+operator+","+field_key+"="+field_value+"不满足条件");
                                return false;
                            }
                            break;
                        default:
                            if (flag == 0) {
                                // System.err.println("operator:"+operator+","+field_key+"="+field_value+"不满足条件");
                                return false;
                            }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;

    }


    /**
     * 获取redis 分组key
     *
     * @return
     */
    public String getGroupRedisKey() {
        StringBuilder dim_key = new StringBuilder();
        if (tableMap == null) {
            System.err.println("getGroupRedisKey 语法错误,请重新配置sql模板");
            return null;
        }
        if (tableMap.get(Constant.GROUP) == null) {
            System.err.println("没有可计算的维度");
            return null;
        }
        try {

            Set<TableStat.Column> columns = (Set<TableStat.Column>) tableMap.get(Constant.GROUP);
            Iterator<TableStat.Column> iterator = columns.iterator();
            while (iterator.hasNext()) {
                TableStat.Column column = iterator.next();
                String column_name = column.getName();
                try {
                    //根据group条件反射 在日志中对应字段的值
                    Class classz = log.getClass();
                    Field field = classz.getDeclaredField(column_name);
                    field.setAccessible(true);
                    Object obj = field.get(log);// 获取日志字段值
                    String field_value = obj == null ? "null" :obj.toString();
                    dim_key.append(column_name).append("=").append(field_value);
                    if (iterator.hasNext()) {
                        dim_key.append("&");
                    }
                } catch (Exception e) {
                    continue;
                }
            }

        } catch (Exception e) {
            System.err.println(e);
            return null;
        }

        return dim_key.toString();

    }


    public static void main(String[] args) {
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public T getLog() {
        return log;
    }

    public void setLog(T log) {
        this.log = log;
    }

    public Map<String, Object> getTableMap() {
        return tableMap;
    }

    public void setTableMap(Map<String, Object> tableMap) {
        this.tableMap = tableMap;
    }
}
