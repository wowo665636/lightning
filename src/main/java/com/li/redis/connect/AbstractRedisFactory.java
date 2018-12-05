package com.li.redis.connect;

import org.apache.commons.lang.StringUtils;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by wangdi on 17/7/27.
 */
public abstract class AbstractRedisFactory {

    private Map<String, Object> redisConfig;

    public Properties propsPrefix(final String prefix) {
        final Properties prop = new Properties();
        for (Map.Entry<String, Object> entry : redisConfig.entrySet()) {
            if (entry.getKey().startsWith(prefix)) {
                prop.setProperty(entry.getKey().substring(prefix.length()), String.valueOf(entry.getValue()));
            }
        }
        return prop;
    }

    public Properties propsPreNone() {
        final Properties prop = new Properties();
        for (Map.Entry<String, Object> entry : redisConfig.entrySet()) {
            if (StringUtils.isNotBlank(entry.getKey())) {
                prop.setProperty(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }
        return prop;
    }

    public abstract HashMap<Integer,RedisClient> initPools();

    public abstract JedisCluster initCluster();


    public void setRedisConfig(Map<String, Object> redisConfig) {
        this.redisConfig = redisConfig;
    }


    public Map<String, Object> getRedisConfig() {
        return redisConfig;
    }



    public AbstractRedisFactory(Map<String, Object> config) {
        this.redisConfig = config;
    }


}
