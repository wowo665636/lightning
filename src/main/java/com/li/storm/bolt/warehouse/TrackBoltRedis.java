package com.li.storm.bolt.warehouse;

import com.li.core.AbstractTrackLog;
import com.li.core.TdwdStormAppid;
import com.li.redis.Constant;
import com.li.redis.connect.RedisClient;
import com.li.redis.connect.RedisHandler;
import com.li.util.CommonJsonUtil;
import com.li.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by wangdi on 18/7/23.
 */
public class TrackBoltRedis extends AbstractTrackLog implements IRichBolt {
    private static final Logger logger = LoggerFactory.getLogger(TrackBoltRedis.class);
    private static final int EXPIRE_TIME = 24*60*60;
    private static final int EXPIRE_TIME_HOUR = 60*60;
    private static final long serialVersionUID = 1971543959630688453L;
    private OutputCollector collector;
    private Map<Integer,RedisClient> clientMap ;
    private final String table_ad_position="t_dwd_xps_ad_position";
    private Map<String,String> ad_position_map;
    private final String v="v";
    private final String na="na";
    private final String av="av";
    private final String click="click";
    private final String naa="naa";


    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector = outputCollector;
        try{
            clientMap = RedisHandler.getInstance();
            ad_position_map = clientMap.get(0).hgetAll(table_ad_position);
        }catch (Exception e){
            logger.error("jedis is null");
        }
    }

    @Override
    public void execute(Tuple tuple) {
        try{
            String key  = tuple.getStringByField("key");
            String value_json  = tuple.getStringByField("value");
            logger.info("message_map:"+ value_json);
            if(StringUtils.isBlank(key)){
                collector.ack(tuple);
                return ;
            }
            if(StringUtils.isBlank(value_json)){
                collector.ack(tuple);
                return ;
            }
            switch (key){
                case "dwd_appid":
                    String key_head = Constant.BI_WAREHOUSE_+"dwd_appid_";
                    TdwdStormAppid tDwdAppidModel = (TdwdStormAppid) CommonJsonUtil.jsonToUserObject(value_json,TdwdStormAppid.class);
                    if(tDwdAppidModel==null){
                        logger.error("t_dwd_appid cover obj is failed,value="+value_json);
                        collector.ack(tuple);
                        return;
                    }
                    DateUtil dateUtil = new DateUtil();
                    String ett = tDwdAppidModel.getEtt();
                    /** 添加维表关联信息**/
                    tDwdAppidModel.setIs_effect(ad_position_map.get(tDwdAppidModel.getAdslotid()));

                    String uniqueKey  = tDwdAppidModel.getUniquenKey();// 设置 unique key


                    int hashCode = Math.abs(uniqueKey.hashCode())%clientMap.size();
                    RedisClient client = clientMap.get(hashCode);
                    /**设置小时时间戳*/
                    String date_time = dateUtil.timestamp2DateMinute(tDwdAppidModel.getTimestamp());
                    String day = date_time.substring(0,8);
                    //String dayHour = date_time.substring(0,10);
                    /**设置请求数*/
                    long count_num = client.hincrBy(key_head+Constant.NUM_+day,uniqueKey,1);
                    client.expire(key_head + Constant.NUM_ + day,EXPIRE_TIME);
                    //lient.hincrBy(key_head+Constant.NUM_+date_time,uniqueKey,1); //按分钟存储数据
                    //client.expire(key_head+Constant.NUM_+date_time,EXPIRE_TIME_HOUR);

                    /**设置计费金额**/
                    String charge = tDwdAppidModel.getCharge();
                    if(StringUtils.isBlank(charge)){
                        charge="0";
                    }
                    long sum_charge =  client.hincrBy(key_head+Constant.CONSUME_+day,uniqueKey,Long.valueOf(charge));
                     client.expire(key_head+Constant.CONSUME_+day,EXPIRE_TIME);
                     //client.hincrBy(key_head+Constant.CONSUME_+date_time,uniqueKey,Long.valueOf(charge));
                     //client.expire(key_head+Constant.CONSUME_+date_time,EXPIRE_TIME_HOUR);
                    break;

                default:
                    System.out.println("default ~~~~");
            }

        }catch (Exception e){
            logger.error("TrackBoltRedis exception :",e);
        }
        collector.ack(tuple);
    }






    @Override
    public void cleanup() {

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("key", "value"));
        //outputFieldsDeclarer.declare(new Fields("message"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
