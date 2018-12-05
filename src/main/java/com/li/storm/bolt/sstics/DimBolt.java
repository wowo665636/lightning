package com.li.storm.bolt.sstics;

import com.alibaba.fastjson.JSON;
import com.li.core.AbstractTrackLog;
import com.li.redis.Constant;
import com.li.redis.cluster.RedisClusterHandler;
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
import redis.clients.jedis.JedisCluster;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangdi on 18/04/13. IRichBolt
 */
public class DimBolt extends AbstractTrackLog implements IRichBolt {
    private static final Logger log = LoggerFactory.getLogger(DimBolt.class);
    private static final long serialVersionUID = 9192328487342984477L;
    private OutputCollector collector;
    private static JedisCluster jedisCluster;
    private static final int EXPIRE_TIME = 24*60*60;
    private final String v="v";
    private final String na="na";
    private final String av="av";
    private final String click="click";
    private final String naa="naa";

    private static final String c1="appid";
    private static final String c2="ssid";
    private static final String c3="plat";
    private static final String c4="p";



    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector = outputCollector;
        try{
            jedisCluster = RedisClusterHandler.getInstance();
        }catch (Exception e){
            log.error("jedis is null");
        }
    }

    @Override
    public void execute(Tuple tuple) {
        try{
            String message = (String)tuple.getValue(0);
            log.info("dim_message:"+message);
            //指标聚合维度sql
            Map<String, String>  dim_map = jedisCluster.hgetAll(Constant.BI_DIM_RULE);
            if(dim_map==null){
                log.error("尚未定制维度聚合模板,请配置sql模板");
                collector.ack(tuple);
                return ;
            }
            // 根据 dim_tuple 地址符解析数据
            AbstractTrackLog trackLog = super.build(message,super.dim_tuple);
            String ett = trackLog.getEtt();
            String timestamp = trackLog.getTimestamp();
            DateUtil dateUtil = new DateUtil();
            //String day_hour =  dateUtil.timestamp2Hour(timestamp);
            String day_key = dateUtil.timestamp2Day(timestamp);
            Class classz =  trackLog.getClass();
            for(Map.Entry<String,String> entry: dim_map.entrySet()){
                String key_head = entry.getKey();
                String appid =  trackLog.getAppid();
                if(Constant.UNION_CROSS_TABLE.equals(key_head)&&"news".equals(appid)){
                    //  混投竞价对news 部分渠道流量 归结到newssdk 特殊处理
                    String pid =  trackLog.getPid();
                        if("6122".equals(pid)|| "6499".equals(pid)||"6500".equals(pid)||"6501".equals(pid)
                                ||"6508".equals(pid)||"6520".equals(pid)||"6521".equals(pid)||"6530".equals(pid)
                                ||"6537".equals(pid)||"6910".equals(pid)||"6922".equals(pid)||"6923".equals(pid)
                                ||"6522".equals(pid)){
                            trackLog.setAppid("newssdk");
                            System.out.println("message_bid:"+message);
                    }

                }
                CustomizationMonitor customization  = new CustomizationMonitor(jedisCluster,trackLog);
                String dim_key = customization.execute(entry.getKey(),entry.getValue(),classz);
                //System.out.println("dim_key:"+dim_key+",rule_key="+entry.getKey()+",rule_value="+entry.getValue());

                if(StringUtils.isBlank(dim_key)){
                   // log.error("没有满足聚合条件的key,sql_key:"+entry.getKey());
                   continue;
                }
                String reids_key_head=Constant.BI_CUSTOM_+key_head+"_";
                /**4.计算 加载/展示/点击/收入*****/
                   switch (ett){
                        case na:
                            jedisCluster.hincrBy(reids_key_head+Constant.NA_+day_key,dim_key.toString(),1);
                            jedisCluster.expire(reids_key_head+Constant.NA_+day_key,EXPIRE_TIME);
                            break;
                       case naa:
                           jedisCluster.hincrBy(reids_key_head+Constant.NAA_+day_key,dim_key.toString(),1);
                           jedisCluster.expire(reids_key_head+Constant.NAA_+day_key,EXPIRE_TIME);
                           break;
                        case v:
                            jedisCluster.hincrBy(reids_key_head+Constant.V_+day_key,dim_key.toString(),1);
                            jedisCluster.expire(reids_key_head+Constant.V_+day_key,EXPIRE_TIME);
                            /**4.计算收入  cpm*/
                            if(trackLog.checkIncome(trackLog)&& "2".equals(trackLog.getBidtype())) {
                                jedisCluster.hincrBy(reids_key_head+Constant.CONSUME_ + day_key, dim_key.toString(), Long.valueOf(trackLog.getCharge()));
                                jedisCluster.expire(reids_key_head+Constant.CONSUME_ + day_key, EXPIRE_TIME);
                            }
                            break;
                        case av:
                            if(!trackLog.checkAcIncome(trackLog)){
                                break;
                            }
                            jedisCluster.hincrBy(reids_key_head+Constant.AV_+day_key,dim_key.toString(),1);
                            jedisCluster.expire(reids_key_head+Constant.AV_+day_key,EXPIRE_TIME);
                            if(trackLog.checkIncome(trackLog)&&"4".equals(trackLog.getBidtype())){
                                jedisCluster.hincrBy(reids_key_head+Constant.CONSUME_ + day_key, dim_key.toString(), Long.valueOf(trackLog.getCharge()));
                                jedisCluster.expire(reids_key_head+Constant.CONSUME_ + day_key, EXPIRE_TIME);
                            }
                            break;
                        case click:
                            jedisCluster.hincrBy(reids_key_head+Constant.C_+ day_key,dim_key.toString(),1);
                            jedisCluster.expire(reids_key_head+Constant.C_+ day_key,EXPIRE_TIME);
                            /***4.计算收入  cpc*/
                            if(trackLog.checkIncome(trackLog)&&"3".equals(trackLog.getBidtype())){
                                jedisCluster.hincrBy(reids_key_head+Constant.CONSUME_ + day_key, dim_key.toString(), Long.valueOf(trackLog.getCharge()));
                                jedisCluster.expire(reids_key_head+Constant.CONSUME_  + day_key, EXPIRE_TIME);
                            }
                            break;
                        default:
                            log.info("此类型日志,暂不出处理,ett="+ett);
                    }

            }
            collector.ack(tuple);
            return ;
        }catch (Exception e){
            log.error("DimBolt业务处理异常");
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

    public static void main(String[] args) {
        DimBolt bolt  = new DimBolt();
        String message="ett=v&accounttype=1&appid=pcnews&charge=420000&bidtype=3&appdelaytrack=null&adslotid=15313&reposition=1&lc=null&rr=null&isspam=0";
        AbstractTrackLog trackLog = bolt.build(message,bolt.dim_tuple);
        String ett = trackLog.getEtt();
        String timestamp = trackLog.getTimestamp();
        DateUtil dateUtil = new DateUtil();
        String day_hour =  dateUtil.timestamp2Hour(timestamp);
        System.out.println(JSON.toJSONString(trackLog));
        System.out.println(day_hour);

    }
}
