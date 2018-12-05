package com.li.storm.bolt.warehouse;

import com.alibaba.fastjson.JSON;
import com.li.core.AbstractTrackLog;
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
 * Created by wangdi on 18/7/23.
 */
public class TrackBoltOds extends AbstractTrackLog implements IRichBolt {
    private static final long serialVersionUID = -3769145230788515899L;
    private static final Logger logger = LoggerFactory.getLogger(TrackBoltOds.class);
    private OutputCollector collector;
    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector = outputCollector;
    }

    @Override
    public void execute(Tuple tuple) {

        try{
            String message = (String)tuple.getValue(4);
            //logger.info("message:"+message);
            if(StringUtils.isBlank(message)){
                collector.ack(tuple);
                return ;
            }
            /**1.初始 track model*/
            AbstractTrackLog trackLog = super.build(message,super.sp_tuple);
             /**过滤作弊数据,暂时不加,保持跟离线数据一致*/
            /*if (super.checkIsSpam(trackLog)){
                collector.ack(tuple);
                return ;
            }*/
            if(!checkBusinessData(trackLog)){
                collector.ack(tuple);
                return ;
            }
            String track_ods_json = JSON.toJSONString(trackLog);
            collector.emit(new Values(track_ods_json));
        }catch (Exception e){
            logger.error("TrackBoltOds execute exception:",e);
        }
        collector.ack(tuple);
        return ;

    }

    @Override
    public void cleanup() {
        logger.info("TrackBoltOds is close");

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("message_ods"));

    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
