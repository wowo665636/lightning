package com.li.test;

import com.li.redis.Constant;
import com.li.redis.cluster.RedisClusterHandler;
import org.apache.commons.lang.StringUtils;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import redis.clients.jedis.JedisCluster;

import java.util.Map;

/**
 * Created by wangdi on 17/7/6. IRichBolt
 */
public class TrackBolt implements IRichBolt {
    private static final long serialVersionUID = -510005517858352855L;

    private OutputCollector collector;
    private static final int EXPIRE_TIME = 2*60*60;
    private static JedisCluster  jedisCluster;


    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector = outputCollector;
        try{
            jedisCluster = RedisClusterHandler.getInstance();

        }catch (Exception e){
            System.err.println("jedis is null");
        }


    }

    @Override
    public void execute(Tuple tuple) {
        String word = (String) tuple.getValue(0);
        System.out.println("track-click:"+word);
        if(StringUtils.isBlank(word)){
            System.err.println("--kafka message error---:"+word);
            collector.ack(tuple);
            return ;
        }
       try{
           jedisCluster.incr("track_topo");
           jedisCluster.expire("track_topo",EXPIRE_TIME);
        }catch (Exception e){
            System.err.println(e.getMessage() );
        }
        collector.ack(tuple);
    }


    @Override
    public void cleanup() {
        System.out.println("topo is close");
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("message"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }







    public OutputCollector getCollector() {
        return collector;
    }

    public void setCollector(OutputCollector collector) {
        this.collector = collector;
    }

}
