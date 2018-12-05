package com.li;

/**
 * Created by wangdi on 17/7/10.
 */
import com.li.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCommands;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Date;

public class TestJedis {
    /*public static final Logger logger = LoggerFactory.getLogger(TestJedis.class);
    // Jedispool
    JedisCommands jedisCommands;
    JedisPool jedisPool;
    JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
    String ip = "m.redis.sohucs.com";
    String password = "04b771425a0b6f1c3ddabaf6e61ffea7";
    int port = 22149;
    int timeout = 2000;

    public TestJedis() {
        // 初始化jedis
        // 设置配置
        jedisPoolConfig.setMaxTotal(1024);
        jedisPoolConfig.setMaxIdle(100);
        jedisPoolConfig.setMaxWaitMillis(100);
        jedisPoolConfig.setTestOnBorrow(false);//jedis 第一次启动时，会报错
        jedisPoolConfig.setTestOnReturn(true);
        // 初始化JedisPool
        jedisPool = new JedisPool(jedisPoolConfig, ip, port, timeout,password);
        //
        Jedis jedis = jedisPool.getResource();

        jedisCommands = jedis;
    }

    public void setValue(String key, String value) {
        this.jedisCommands.set(key, value);
    }

    public String getValue(String key) {
        return this.jedisCommands.get(key);
    }*/

    public static void main(String[] args) {
        //TestJedis testJedis = new TestJedis();
       // testJedis.setValue("testJedisKey", "testJedisValue");
       // logger.info("get value from redis:{}",testJedis.getValue("testJedisKey"));

       // Jedis jedis = JedisPoolUtil.getJedis();
        //System.out.println(jedis.get("testJedisKey"));
        String currentTime =  DateUtil.getCurrentDateMM();
        System.out.println("mm".equals(null));
      /*  Date date = new Date();
        for(int i=0; i<=5 ; i++){
            String  load_key =   DateUtil.diffDateTime(date,(i+5)*60*1000);
            System.out.println("----load_key="+load_key+",currentTime="+currentTime);

        }*/
    }

}