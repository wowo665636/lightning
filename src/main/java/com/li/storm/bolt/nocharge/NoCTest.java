package com.li.storm.bolt.nocharge;

import com.alibaba.fastjson.JSON;
import com.li.core.AbstractTrackNoChargeLog;
import com.li.redis.Constant;
import com.li.redis.cluster.RedisClusterHandler;
import com.li.storm.bolt.sstics.CustomizationMonitor;
import redis.clients.jedis.JedisCluster;

import java.util.Map;

/**
 * Created by wangdi on 18/9/7.
 */
public class NoCTest {
    public static void main(String[] args) {
        JedisCluster jedisCluster =  RedisClusterHandler.getInstance();
        String message="clickid=325476609047265280c3445d8c8fb107c8_0_0\tatype=1\ttimestamp=1537171580132\tmeta=b4TG0p5dq8mpdeX7P3XNRZWuFRbAvA1vP/DdPx6Zi1eGMAblw3Vn1Q9mojLERWavJ2rOSF6qLx009WIHSOB6mj8VU9m3bKhVstbLy6WqalJm8eXiDKYFIAn7b0Z3rMuR\taid=10017420\tcampid=20057608\tadgid=40223006\tcrsid=100446528\tcrid=101019009\ttt3=1537171580131\tett=ac1";
        AbstractTrackNoChargeLog noChargeLog =new AbstractTrackNoChargeLog();
        noChargeLog = noChargeLog.build(message,"\t");

        if(noChargeLog==null){
            System.err.println("noChargeLog is null,message="+message);
            return ;
        }
        Map<String, String> rule_map = jedisCluster.hgetAll(Constant.BI_CUSTOM_NOCHARGE_RULE);
        if(rule_map==null){
            return;
        }
        Class classz = noChargeLog.getClass();
        for(Map.Entry<String,String> entry:rule_map.entrySet()){
            System.out.println(entry.getKey()+":"+entry.getValue());
            System.out.println(JSON.toJSONString(noChargeLog));
            CustomizationMonitor customization  = new CustomizationMonitor(jedisCluster,noChargeLog);
            String dim_key = customization.execute(entry.getKey(),entry.getValue(),classz);
            System.out.println("----result----"+dim_key);
        }
    }



}
