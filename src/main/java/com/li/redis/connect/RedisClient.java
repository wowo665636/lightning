package com.li.redis.connect;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Map;
import java.util.Set;

public class RedisClient {
    private static final Logger log = LoggerFactory.getLogger(RedisClient.class);
    private JedisPool jedisPool ;


    public RedisClient (JedisPool jedisPool){
        this.jedisPool = jedisPool;
    }

    /**
     * 差集：set1中有，set2中没
     * @return
     */
    public Set<String> sdiff(String...keys) {
        Set<String> result = null;
        Jedis jedis = jedisPool.getResource();
        if (jedis == null) {
            return result;
        }
        try {
            result = jedis.sdiff(keys);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            jedis.close();
        }
        return result;
    }

    /**
     * 设置单个值
     *
     * @param key
     * @param value
     * @return
     */
    public String set(String key, String value) {
        String result = null;
        Jedis jedis = jedisPool.getResource();
        if (jedis == null) {
            return result;
        }
        try {
            result = jedis.set(key, value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            jedis.close();
        }
        return result;
    }

    /**
     * 获取单个值
     *
     * @param key
     * @return
     */
    public String get(String key) {
        String result = null;
        Jedis jedis = jedisPool.getResource();
        if (jedis == null) {
            return result;
        }
        boolean broken = false;
        try {
            result = jedis.get(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            broken = true;
        } finally {
            jedis.close();
        }
        return result;
    }

    /**
     * redis lpop
     * @param key
     * @return
     */
    public String lpop(String key) {
        String result = null;
        Jedis jedis = jedisPool.getResource();
        if (jedis == null) {
            return result;
        }
        try {
            result = jedis.lpop(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            jedis.close();
        }
        return result;
    }

    /**
     * lpop byte
     * @param key
     * @return
     */
    public byte[] lpop(byte[] key) {
        byte[] result = null;
        Jedis jedis = jedisPool.getResource();
        if (jedis == null) {
            return result;
        }
        boolean broken = false;
        try {
            result = jedis.lpop(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            jedis.close();
        }
        return result;
    }

    /**
     * redis llen
     * @param key
     * @return
     */
    public Long llen(String key) {
        Long result = null;
        Jedis jedis = jedisPool.getResource();
        if (jedis == null) {
            return result;
        }
        boolean broken = false;
        try {
            result = jedis.llen(key);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            broken = true;
        } finally {
            jedis.close();
        }
        return result;
    }



    /**
         * redis rpush
         * @param key
         * @param string
         * @return
         */
    public Long rpush(String key, String string) {
        Long result = null;
        Jedis jedis = jedisPool.getResource();
        if (jedis == null) {
            return result;
        }
        try {
            result = jedis.rpush(key, string);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            jedis.close();
        }
        return result;
    }

    public Long rpush(byte[] key, byte[] string) {
        Long result = null;
        Jedis jedis = jedisPool.getResource();
        if (jedis == null) {
            return result;
        }
        try {
            result = jedis.rpush(key, string);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            jedis.close();
        }
        return result;
    }


    /**
     * redis hget
     * @param key
     * @param field
     * @return
     */
    public String hget(String key, String field) {
        String result = null;
        Jedis jedis = jedisPool.getResource();
        if (jedis == null) {
            return result;
        }
        try {
            result = jedis.hget(key, field);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            jedis.close();
        }
        return result;
    }

    /**
     * hgetall
     * @param key
     * @return
     */
    public Map<String, String> hgetAll(String key) {
        Map<String, String> result = null;
        Jedis jedis = jedisPool.getResource();
        if (jedis == null) {
            return result;
        }
        try {
            result = jedis.hgetAll(key);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            jedis.close();
        }
        return result;
    }

    /**
     * redis hset
     * @param key
     * @param field
     * @param value
     * @return
     */
    public Long hset(String key, String field, String value) {
        Long result = null;
        Jedis jedis = jedisPool.getResource();
        if (jedis == null) {
            return result;
        }
        try {
            result = jedis.hset(key, field, value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            jedis.close();
        }
        return result;
    }

    /**
     * redis hincr
     * @param key
     * @param field
     * @param value
     * @return
     */
    public Long hincrBy(String key, String field, long value) {
        Long result = null;
        Jedis jedis = jedisPool.getResource();
        if (jedis == null) {
            return result;
        }
        try {
            result = jedis.hincrBy(key, field, value);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            jedis.close();
        }
        return result;
    }

    /**
     * incr
     * @param key
     * @return
     */
    public Long incr(String key) {
        Long result = null;
        Jedis jedis = jedisPool.getResource();
        if (jedis == null) {
            return result;
        }
        try {
            result = jedis.incr(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
           jedis.close();
        }
        return result;
    }

    /**
     * incr
     * @param key
     * @return
     */
    public Double incrByFloat(String key,double value) {
        Double result = null;
        Jedis jedis = jedisPool.getResource();
        if (jedis == null) {
            return result;
        }
        try {
            result = jedis.incrByFloat(key,value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            jedis.close();
        }
        return result;
    }

    /**
     * hIncrByFloat
     * @param key
     * @param value
     * @return
     */
    public Double hIncrByFloat(String hkey,String key,double value) {
        Double result = null;
        Jedis jedis = jedisPool.getResource();
        if (jedis == null) {
            return result;
        }
        try {
            result = jedis.hincrByFloat(hkey,key,value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            jedis.close();
        }
        return result;
    }



    /**
     * 失效时间设置
     * @param key
     * @param seconds
     * @return
     */
    public Long expire(String key, int seconds) {
        Long result = null;
        Jedis jedis = jedisPool.getResource();
        if (jedis == null) {
            return result;
        }
        try {
            result = jedis.expire(key, seconds);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            jedis.close();
        }
        return result;
    }

    public JedisPool getJedisPool() {
        return jedisPool;
    }

    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }
}
