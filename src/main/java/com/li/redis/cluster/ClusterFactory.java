package com.li.redis.cluster;

import com.li.redis.Constant;
import com.li.redis.connect.AbstractRedisFactory;
import com.li.redis.connect.RedisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.util.*;

/**
 * Created by wangdi on 17/11/1.
 */
public class ClusterFactory extends AbstractRedisFactory {

    private static final Logger logger= LoggerFactory.getLogger(ClusterFactory.class);

    public ClusterFactory(Map<String, Object> config) {
        super(config);
    }

    @Override
    public HashMap<Integer, RedisClient> initPools() {
        return null;
    }

    @Override
    public JedisCluster initCluster() {
        return this.createJedisCluster(propsPreNone());
    }

    /**
     * 创建JedisCluster
     * @param pro
     * @return
     */
    public JedisCluster createJedisCluster(Properties pro){
        Set<HostAndPort> jedisClusterNode = new HashSet<HostAndPort>();
        String redis_addrs = pro.getProperty("REDIS_ADDR");
        JedisPoolConfig DEFAULT_CONFIG = new JedisPoolConfig();
        JedisCluster jc = null;
        try {
            if(redis_addrs.indexOf(Constant.REDIS_CLUSTER_SEG)!=-1){
                String[] redis_addr_arr = redis_addrs.split(Constant.REDIS_CLUSTER_SEG);
                for(String redis_addr:redis_addr_arr){
                    jedisClusterNode.add(new HostAndPort(redis_addr.split(Constant.REDIS_CLUSTER_HOST_SEG)[0], Integer.valueOf(redis_addr.split(Constant.REDIS_CLUSTER_HOST_SEG)[1])));
                }
            }else {
                jedisClusterNode.add(new HostAndPort(redis_addrs.split(Constant.REDIS_CLUSTER_HOST_SEG)[0], Integer.valueOf(redis_addrs.split(Constant.REDIS_CLUSTER_HOST_SEG)[1])));
            }

            jc = new JedisCluster(jedisClusterNode, Integer.valueOf(pro.getProperty("DEFAULT_TIMEOUT")), Integer.valueOf(pro.getProperty("DEFAULT_TIMEOUT")),
                    Integer.valueOf(pro.getProperty("DEFAULT_REDIRECTIONS")) ,pro.getProperty("PASS_WORD"), DEFAULT_CONFIG);
        } catch (Exception e){
            logger.error("jedis-cluster exception.",e);
            try {
                jc.close();
            } catch (IOException e1) {
                e.printStackTrace();
            }
        }
        return jc;
    }




}
