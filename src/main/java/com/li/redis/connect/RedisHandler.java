package com.li.redis.connect;


import com.li.util.ConfigRedisStorm;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangdi on 17/7/27.
 */
public class RedisHandler {

    public RedisHandler(){}

    public static class SingleHolder{

        private static Map<Integer,RedisClient> clientMap = new HashMap<>();
        private static SingleHolder instance = new SingleHolder();

        public SingleHolder(){
            try{
                HashMap<Integer,RedisClient> client_bi = new RedisFactory(ConfigRedisStorm.getProMap()).createClient();
                clientMap = client_bi;
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    public static  Map<Integer,RedisClient> getInstance(){

        return SingleHolder.clientMap;
    }




}
