package com.li.core;

import com.li.util.SqlParserTool;

/**
 * Created by wangdi on 18/4/10.
 */
public class SQLParserFactory<T> {
    /**
     * 构建广告日志 sql 解析工具
     * @param sql
     * @return
     */
    public static  <T> SqlParserTool<T> createTrackSqlParserTool(String sql, T trackLog) {
        return new SqlParserTool(sql,trackLog);
    }

    /**
     * 够条件请求日志sql 解析工具
     * @param sql
     * @return
     */
    public static  <T> SqlParserTool createSearchSqlParserTool(String sql,T searchLog) {
        return new SqlParserTool(sql,searchLog);
    }

}
