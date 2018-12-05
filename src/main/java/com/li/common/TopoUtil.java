package com.li.common;

import com.li.redis.Constant;
import com.li.util.ConfigTopoStorm;
import org.apache.storm.Config;
import org.apache.storm.kafka.spout.KafkaSpoutConfig;

import java.util.Arrays;
import java.util.Properties;

/**
 * Created by wangdi on 18/4/2.
 */
public class TopoUtil {
    private String group_id ;
    static String nimbus_seeds = ((String) ConfigTopoStorm.getProMap().get(Constant.NIMBUS_SEEDS));
    static String kafka_broker = (String) ConfigTopoStorm.getProMap().get(Constant.KAFKA_BROKER);


    public TopoUtil(String group_id){
        this.group_id = group_id;

    }
    /**
     * 构建 kafkaSpoutConfig
     * @param topics
     * @return
     */
    public  KafkaSpoutConfig bulidKafkaSpoutConfig(String ... topics ){
        System.out.println(kafka_broker);
        System.out.println(group_id);
        return   KafkaSpoutConfig.builder(kafka_broker, topics)
                .setOffsetCommitPeriodMs(1000)
                .setProp(getConsumerProperties(group_id))
                .setMaxUncommittedOffsets(1000)
                .setFirstPollOffsetStrategy(KafkaSpoutConfig.FirstPollOffsetStrategy.UNCOMMITTED_LATEST)
                //.setRecordTranslator(new RecordTranslatorValueTimestamp<>() )
                .build();
    }
    /**
     * 获取消费 mp kafka 集群配置
     * @return
     */
    private  Properties getConsumerProperties(String group_id){
        Properties props = new Properties();
        props.setProperty("group.id", group_id);
        props.setProperty("enable.auto.commit", "true");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("acks", "1");
        return props;
    }
    /**
     * 获取storm配置
     * @return
     */
    public   Config getConfig(int workers) {
        Config config = new Config();
        System.out.println(nimbus_seeds);
        config.put(Config.NIMBUS_SEEDS, Arrays.asList(nimbus_seeds));
        //config.setMaxSpoutPending(100);
        config.setMessageTimeoutSecs(60);
        config.setNumWorkers(workers);
        config.setDebug(false);
        config.setMessageTimeoutSecs(10000000);
        return config;
    }


    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }
}
