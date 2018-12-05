package com.li;

import com.li.redis.cluster.RedisClusterHandler;
import redis.clients.jedis.JedisCluster;

/**
 * Created by wangdi on 17/7/28.
 */
public class RedisClusterTest {
    public static void main(String[] args) {
        JedisCluster jedisCluster = RedisClusterHandler.getInstance();
        //jedisCluster.set("test_ccc","sohu-123");
        System.out.println(jedisCluster.get("test_ccc"));

    }

}
