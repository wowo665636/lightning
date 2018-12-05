package com.li.storm.bolt.sstics;

import com.li.core.AbstractTrackLog;
import com.li.redis.Constant;
import com.li.redis.cluster.RedisClusterHandler;
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
 * Created by wangdi on 2018-04-13. IRichBolt
 * 够条件fact_storm_table,此层sql 一定要有ett 字段作为 维度
 */
public class CustomizationMonitorBolt extends AbstractTrackLog implements IRichBolt {
    private static final Logger log = LoggerFactory.getLogger(CustomizationMonitorBolt.class);
    private static final long serialVersionUID = 2932219457538305920L;
    private OutputCollector collector;
    private static JedisCluster jedisCluster;

    /**
     * 初始化bolt
     * @param map
     * @param topologyContext
     * @param outputCollector
     */
    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector = outputCollector;
        try{
            jedisCluster = RedisClusterHandler.getInstance();
        }catch (Exception e){
            log.error("jedis is null");
        }
    }

    /**
     * 循环处理每个tuple
     * @param tuple
     */
    @Override
    public void execute(Tuple tuple) {
        try{
            String message = (String)tuple.getValue(4);
            //log.info("message:"+message);
            if(StringUtils.isBlank(message)){
                log.error("--kafka message error---:"+message);
                collector.ack(tuple);
                return ;
            }
            /**1.初始 track model*/
            AbstractTrackLog trackLog = super.build(message,super.sp_tuple);
            /**过滤作弊数据*/
           /* if (super.checkIsSpam(trackLog)){
                collector.ack(tuple);
                return ;
            }*/
            /**过滤错误数据*/
            if(!checkBusinessData(trackLog)){
                collector.ack(tuple);
                return ;
            }
            String id = trackLog.getId();
            String time_ = trackLog.getTimestamp();
            if(id.length()>30){
                trackLog.setId(id.substring(0,30));
            }
            if(time_.length()>13){
                trackLog.setTimestamp( time_.substring(0,13));
            }
            /**1.解析sql**/
            Map<String, String>  rule_map = jedisCluster.hgetAll(Constant.BI_CUSTOM_RULE);
            if(rule_map==null){
                log.error("尚未定制模板,请配置sql模板");
                collector.ack(tuple);
            }
            /***逐个规则过滤,生成纬度值****/
            Class classz =  trackLog.getClass();
            for(Map.Entry<String,String> entry: rule_map.entrySet()){
                CustomizationMonitor customization  = new CustomizationMonitor(jedisCluster,trackLog);
                String dim_key = customization.execute(entry.getKey(),entry.getValue(),classz);
                // 产生fact 大宽key 就向下游发送
                if(!StringUtils.isBlank(dim_key)){
                    collector.emit(new Values(dim_key));
                }
            }

            collector.ack(tuple);
            return ;
        }catch (Exception e){
            log.error("业务处理异常");
            e.printStackTrace();
            collector.ack(tuple);
            return ;

        }

    }

    @Override
    public void cleanup() {
        log.info("TrackingNoChargeBolt is close");
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("dim_key"));
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
