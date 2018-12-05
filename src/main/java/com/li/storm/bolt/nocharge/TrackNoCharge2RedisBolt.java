package com.li.storm.bolt.nocharge;

import com.li.core.AbstractTrackNoChargeLog;
import com.li.redis.Constant;
import com.li.redis.cluster.RedisClusterHandler;
import org.apache.commons.lang.StringUtils;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisCluster;

import java.util.Map;

/**
 * Created by wangdi on 18/9/11.
 */
public class TrackNoCharge2RedisBolt extends AbstractTrackNoChargeLog implements IRichBolt {


    private static final long serialVersionUID = -6482459701669619985L;
    private static final int EXPIRE_TIME = 24*60*60;
    private static final Logger logger = LoggerFactory.getLogger(TrackNoChargeEtlBot.class);
    private OutputCollector collector;
    private static JedisCluster jedisCluster;

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector = outputCollector;
        try{
            jedisCluster = RedisClusterHandler.getInstance();
        }catch (Exception e){
            logger.error("jedis is null");
        }
    }

    @Override
    public void execute(Tuple tuple) {
        try{
            String r_table  = tuple.getStringByField("key");
            String dim_key  = tuple.getStringByField("value");
            if(StringUtils.isBlank(r_table)){
                System.out.printf("table key is not null");
                collector.ack(tuple);
            }
            jedisCluster.hincrBy(Constant.BI_CUSTOM_+r_table,dim_key,1);
            jedisCluster.expire(Constant.BI_CUSTOM_+r_table,EXPIRE_TIME);

        }catch (Exception e){
            logger.error("TrackNoCharge2RedisBolt exception,",e);
        }

        collector.ack(tuple);
        return;
    }

    @Override
    public void cleanup() {
        System.out.println("TrackNoCharge2RedisBolt is shut down");
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
