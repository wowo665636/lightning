package com.li.hive;


import java.sql.*;

/**
 * hive 1.2.2 cdh driver connect test.
 *
 * @author zhaoshb
 * @since 1.0
 */
public class HiveConnectTest {

    //@Test
    public void testConnect() throws ClassNotFoundException, SQLException {
        Class.forName("org.apache.hive.jdbc.HiveDriver");
        Connection conn = DriverManager.getConnection("jdbc:hive2://192.168.202.191:10000", "hadoop", "");
        Statement statement = conn.createStatement();
        statement.executeQuery("select * from test.ptable");
    }
}

