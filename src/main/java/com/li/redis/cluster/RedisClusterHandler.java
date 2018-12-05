package com.li.redis.cluster;


import com.li.util.ConfigRedisCluster;
import redis.clients.jedis.JedisCluster;

/**
 * Created by wangdi on 17/11/1.
 */
public class RedisClusterHandler {

    public RedisClusterHandler(){}

    public static class SingleCluster{
        private static JedisCluster jedisCluster = null;
        private static SingleCluster instance = new SingleCluster();

        public SingleCluster(){
            try{
                 jedisCluster = new ClusterFactory(ConfigRedisCluster.getProMap()).initCluster();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }


    public static JedisCluster getInstance(){
        return SingleCluster.jedisCluster;
    }

}
