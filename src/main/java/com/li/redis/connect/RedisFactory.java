package com.li.redis.connect;


import com.li.redis.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by wangdi on 17/7/27.
 */
public class RedisFactory extends AbstractRedisFactory {

    private static final Logger logger= LoggerFactory.getLogger(RedisFactory.class);

    public RedisFactory(Map<String, Object> config) {
        super(config);
    }

    @Override
    public HashMap<Integer,RedisClient> initPools() {
        return this.createPools(propsPreNone()) ;
    }

    public HashMap<Integer,RedisClient> createClient(){
        return this.initPools();
    }

    @Override
    public JedisCluster initCluster() {
        return null;
    }

    public JedisPool createPool(Properties pro,String ip,String port){
        JedisPool jedisPool = null;
        try{
            JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
            jedisPoolConfig.setMinIdle(Integer.parseInt(pro.getProperty("redis_min_idle")));
            jedisPoolConfig.setMaxIdle(Integer.parseInt(pro.getProperty("redis_max_idle")));
            jedisPoolConfig.setMaxTotal(Integer.parseInt(pro.getProperty("redis_max_active")));
            jedisPoolConfig.setMaxWaitMillis(Long.parseLong(pro.getProperty("redis_max_wait")));
            if("true".equals(pro.getProperty("redis_test_on_borrow"))){
                jedisPoolConfig.setTestOnBorrow(true);
            }else{
                jedisPoolConfig.setTestOnBorrow(false);
            }
            if("true".equals(pro.getProperty("redis_test_on_return"))){
                jedisPoolConfig.setTestOnReturn(true);
            }else{
                jedisPoolConfig.setTestOnReturn(false);
            }
            //String[] redis_addr_arr = pro.getProperty("redis_addr").split(Constant.REDIS_CLUSTER_SEG);
            jedisPool = new JedisPool(jedisPoolConfig, ip,Integer.parseInt(port), Integer.parseInt(pro.getProperty("redis_timeout"))
                    ,pro.getProperty("redis_password"), Integer.parseInt(pro.getProperty("redis_database")));
        }catch(Exception e){
            logger.error("读取Redis配置文件出错:",e);
            return null;
        }

        return jedisPool;

    }

    /**
     * 构建redis pools
     * @param pro
     * @return
     */
    public HashMap<Integer,RedisClient> createPools(Properties pro){
        String[] redis_addr_arr = pro.getProperty("redis_addr").split(Constant.REDIS_CLUSTER_SEG);
        HashMap<Integer,RedisClient> map = new HashMap<>();
        int index=0;
        for(String redis_addr_prot:redis_addr_arr){
            String addr = redis_addr_prot.split(Constant.REDIS_CLUSTER_HOST_SEG)[0];
            String port = redis_addr_prot.split(Constant.REDIS_CLUSTER_HOST_SEG)[1];
            JedisPool jedisPool = createPool(pro,addr,port);
            map.put(index,new RedisClient(jedisPool));
            index++;
        }
        return map;

    }




}
