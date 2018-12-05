package com.li.test;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.stat.TableStat;
import com.li.core.AbstractTrackLog;
import com.li.core.SQLParserFactory;
import com.li.redis.Constant;
import com.li.redis.cluster.RedisClusterHandler;
import com.li.storm.bolt.sstics.CustomizationMonitor;
import com.li.util.SqlParserTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisCluster;

import java.util.*;

/**
 * @Author leizhou215007
 * @Description
 * @date 11:34 2018/6/29
 */
public class TestSqlParser {
    private static final Logger log = LoggerFactory.getLogger(TestSqlParser.class);

    public static void main(String[] args) {

        String sql = "SELECT sum(pv),sum(vv),sum(rpv) from tv_table where appid in ('tv') and ssid in ('pc','wap','app','ott') and plat in('ALL','ipad','gpad','iphone','gphone') and adstyle in ('oad','mad','ead','wrapframe','pad','flogo','fbarad','barrage','page','open','focus','overfly','mp') group by appid,ssid,plat,adstyle";
        Map<String,Object> map =null;// SqlParserTool.parserSql(sql);
     /*   for(Map.Entry entry:map.entrySet()){
            System.out.println("key:"+entry.getKey());
            System.out.println("value:"+entry.getValue());
            System.out.println("------------------");
        }
*/
        Map<String,String> mapWhere = new HashMap<String, String>();
        List<SQLSelectItem> selects = (List<SQLSelectItem>) map.get(Constant.SELECT);
        for(SQLSelectItem select:selects){
            mapWhere.put(String.valueOf(select.getExpr().getChildren().get(0)),"$tp");
        }

        List<TableStat.Condition> fields = (List<TableStat.Condition>) map.get(Constant.WHERE);
        for(TableStat.Condition column:fields){
            String name = column.getColumn().getName();
            List<Object> values = column.getValues();
            for(Object obj:values){
                mapWhere.put(obj.toString(),name);
            }
        }

        Set<TableStat.Column> groups = (Set<TableStat.Column>) map.get(Constant.GROUP);
        Set<String> groupSet = new HashSet<>();
        for(TableStat.Column column:groups){
            groupSet.add(column.getName());
        }






        String key = "pv_pc_oad_2018-05-23 00:00";
        Map<String,String> result = new HashMap<>();
        String[] keyarr = key.split("_");
        if (!keyarr[0].equals("tv")){
            keyarr = ("tv_"+key).split("_");
        }

        String newKey = "";
        String type="";
        for(String k:keyarr){
            String colname = mapWhere.get(k);
            if(colname!=null){
                if(colname.equals("$tp")){
                    type=k;
                }else if(groupSet.contains(colname)){
                    newKey+=colname+"="+k+"&";
                }
            }else{
                newKey+="dtime="+k;
            }
        }
        result.put("type",type);
        result.put("data",newKey);
        System.out.println(type+"--"+newKey);













    /* *//*   SQLStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatement();*//*


        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, "mysql");
        SQLStatement stmt = stmtList.get(0);


        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Columns :"+visitor.getColumns());
        //获取操作方法名称,依赖于表名称
        System.out.println("Manipulation : " + visitor.getTables());
        //获取字段名称
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("where : " + visitor.getConditions());
        //获取字段名称
        System.out.println("group : " + visitor.getGroupByColumns());

        System.out.println("tablename:"+visitor.getAggregateFunctions());




        MySqlSelectQueryBlock queryBlock = (MySqlSelectQueryBlock) ((SQLSelectStatement) stmt).getSelect().getQueryBlock();
        List<SQLSelectItem> list = queryBlock.getSelectList();
        for (SQLSelectItem ssi : list) {
            System.out.println(ssi);
        }

        System.out.println("-------------------------------------------");*/
    }



}
