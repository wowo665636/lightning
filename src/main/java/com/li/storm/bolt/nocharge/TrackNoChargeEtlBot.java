package com.li.storm.bolt.nocharge;

import com.li.core.AbstractTrackNoChargeLog;
import com.li.redis.Constant;
import com.li.redis.cluster.RedisClusterHandler;
import com.li.storm.bolt.sstics.CustomizationMonitor;
import com.li.util.DateUtil;
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
import redis.clients.jedis.JedisCluster;

import java.util.Map;

/**
 * Created by wangdi on 18/9/4.
 */
public class TrackNoChargeEtlBot extends AbstractTrackNoChargeLog implements IRichBolt {
    private static final long serialVersionUID = -1827483935590129025L;
    private static final Logger logger = LoggerFactory.getLogger(TrackNoChargeEtlBot.class);
    private OutputCollector collector;
    private static JedisCluster jedisCluster;
    private static final int EXPIRE_TIME = 24*60*60;

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
            String message = (String)tuple.getValue(4);
            if(StringUtils.isBlank(message)){
                collector.ack(tuple);
                return ;
            }
            System.out.println("message="+message);
            AbstractTrackNoChargeLog noChargeLog = super.build(message,Constant.SP_TUPLE);
            if(noChargeLog==null){
                System.err.println("noChargeLog is null,message="+message);
                collector.ack(tuple);
                return ;
            }
            String timestamp = noChargeLog.getTimestamp();
            if(StringUtils.isBlank(timestamp)){
                System.err.println("noChargeLog is null,timestamp="+timestamp);
                collector.ack(tuple);
                return;
            }
            DateUtil dateUtil = new DateUtil();
            String day_key = dateUtil.timestamp2Day(timestamp);
            String hour_key  =  dateUtil.timestamp2Hour(timestamp);

            Map<String, String>  rule_map = jedisCluster.hgetAll(Constant.BI_CUSTOM_NOCHARGE_RULE);
            if(rule_map==null){
                logger.error("尚未定制模板,请配置sql模板");
                collector.ack(tuple);
            }
            Class classz = noChargeLog.getClass();
            for(Map.Entry<String,String> entry:rule_map.entrySet()){
                String r_table = entry.getKey();
                String r_table_day = r_table+"_"+day_key;
                String r_table_hour = r_table+"_"+hour_key;

                //转化数据去重操作.
                if(Constant.TRANS_CLICK.equals(r_table)&&StringUtils.isNotBlank(noChargeLog.getClickid())){
                    long incr_clickid =  jedisCluster.hincrBy(Constant.BI_CUSTOM_+"uniq_"+r_table_day,noChargeLog.getClickid(),1);
                    jedisCluster.expire(Constant.BI_CUSTOM_+"uniq_"+r_table_day,EXPIRE_TIME);
                    if(incr_clickid>1) {
                        continue;
                    }
                }

                try{
                    CustomizationMonitor customization  = new CustomizationMonitor(jedisCluster,noChargeLog);
                    String dim_key = customization.execute(r_table,entry.getValue(),classz);
                    if(StringUtils.isNotBlank(dim_key)){
                        collector.emit(new Values(r_table_day, dim_key));
                        collector.emit(new Values(r_table_hour, dim_key));
                    }

                }catch (Exception e){
                    logger.error(r_table+" is exception:",e);
                }
             
            }

        }catch (Exception e){
            logger.error("TrackNoChargeEtl exception",e);
        }

        collector.ack(tuple);
        return;

    }



    @Override
    public void cleanup() {
        logger.info("TrackNoChargeEtl is close");
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("key","value"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
