package com.li;

import com.li.redis.RedisObjectSerializer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Created by wangdi on 17/6/21.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisConnectTest {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Test
    public void contextLoads() {
        String aa = "";
        // 保存字符串
        stringRedisTemplate.opsForValue().set("aaa", "111");
        //String aa = stringRedisTemplate.opsForValue().get("aaa");
        System.out.println("------------"+aa);
        RedisTemplate<String, Map>  template = new RedisTemplate<String, Map>();
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new RedisObjectSerializer());

    }
}
