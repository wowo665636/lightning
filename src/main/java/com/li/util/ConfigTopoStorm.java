package com.li.util;


import com.alibaba.fastjson.JSON;
import com.li.redis.connect.RedisClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ConfigTopoStorm {
    private static Properties properties = new Properties();
    private static Map<String,Object> proMap = new HashMap<String, Object>();

    private static final String CONFIG_LOCATION = "topo.properties";
    static {
        InputStream is=null;
        try {
            is = ConfigTopoStorm.class.getClassLoader().getResource(CONFIG_LOCATION).openStream();
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }finally{

            proMap = (Map) properties;
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static  Map<String, Object>  getProMap() {
        return proMap;
    }


    public static void main(String[] args) {
       String[] sb =  ((String)proMap.get("maven.nimbus.host.remote")).split(",");

        System.out.println(JSON.toJSONString(sb));


    }

}
