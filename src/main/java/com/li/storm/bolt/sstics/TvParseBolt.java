package com.li.storm.bolt.sstics;

import com.alibaba.fastjson.JSON;
import com.li.core.AbstractTvLog;
import com.li.redis.Constant;
import org.apache.commons.lang.StringUtils;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by wangdi on 18/7/2.
 */
public class TvParseBolt extends AbstractTvLog implements IRichBolt {
    private static final long serialVersionUID = -7471298027352063885L;
    private static final Logger log = LoggerFactory.getLogger(TvParseBolt.class);
    private OutputCollector collector;
    private final String spilt_reg = "[&?\\s]" ;


    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector = outputCollector;
    }

    @Override
    public void execute(Tuple tuple) {
        try{
            String message = (String)tuple.getValue(4);
            log.info("message:"+message);
            if(StringUtils.isBlank(message)){
                log.error("--kafka message error---:"+message);
                collector.ack(tuple);
                return ;
            }
            AbstractTvLog tvLog = super.build(message,spilt_reg);
            if(tvLog==null){
                log.error("--tvLog is null");
                collector.ack(tuple);
                return ;
            }
            if(StringUtils.isNotBlank(tvLog.getVp())&&!"s".equals(tvLog.getVp())){
                log.error("--vp is error.");
                collector.ack(tuple);
                return ;
            }

            collector.emit(new Values(JSON.toJSONString(tvLog)));

        }catch (Exception e){
            log.error("execute exception :",e);
        }
        collector.ack(tuple);
        return ;

    }

    @Override
    public void cleanup() {

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("message"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
