package com.li.util;


import com.li.redis.connect.RedisClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ConfigRedisStorm {
    private static Properties properties = new Properties();
    private static Map<String,Object> proMap = new HashMap<String, Object>();

    private static final String CONFIG_LOCATION = "redis.properties";
    static {
        InputStream is=null;
        try {
            is = ConfigRedisStorm.class.getClassLoader().getResource(CONFIG_LOCATION).openStream();
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

        Map<String,RedisClient> clientMap = new HashMap<>();
        System.out.println(clientMap.get("mm"));

       /* RedisFactory factory = new RedisFactory(ConfigRedisBI.getProMap());
        Properties pro = factory.propsPreNone();
        JedisPool pool = factory.createPool(pro);
        RedisClient client  = new RedisClient(pool);
        client.set("mm","12311213");
        System.out.println(client.get("mm"));*/
    }

}
