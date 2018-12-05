package com.li.storm.bolt.sstics;

import com.li.core.AbstractTrackLog;
import com.li.redis.Constant;
import com.li.redis.cluster.RedisClusterHandler;
import com.li.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.storm.Config;
import org.apache.storm.Constants;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import redis.clients.jedis.JedisCluster;

import java.util.Map;

/**
 * Created by wangdi on 18/4/4.
 */
public class UvCountBolt extends AbstractTrackLog implements IRichBolt {

    private static final long serialVersionUID = 8174591431487593751L;
    private OutputCollector collector;
    private static JedisCluster jedisCluster;
    private static final int EXPIRE_TIME = 24*60*60;

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
        String message = (String)tuple.getValue(4);
        System.out.println("message:"+message);

        if(StringUtils.isBlank(message)){
            System.err.println("--kafka message error---:"+message);
            collector.ack(tuple);
            return ;
        }
        try{
            /**1.初始 track model*/
            AbstractTrackLog trackLog = super.build(message,super.sp_tuple);
            /**过滤作弊数据*/
            /*if(super.checkIsSpam(trackLog)){
                collector.ack(tuple);
                return ;
            }*/
            /**过滤错误数据*/
            if(!checkBusinessData(trackLog)){
                collector.ack(tuple);
                return ;
            }


            //1.获取dau sql 模板
            String uv_sql = jedisCluster.get(Constant.COUNT_UV);
            if(StringUtils.isBlank(uv_sql)){
                System.err.println("尚未定制维度聚合模板,请配置sql模板");
                collector.ack(tuple);
                return ;
            }
            //count_uv
            Class classz =  trackLog.getClass();
            CustomizationMonitor customization = new CustomizationMonitor(jedisCluster, trackLog);
            //2.解析sql 模板,根据验证条件生成 dim key
            String dim_key = customization.execute(Constant.COUNT_UV,uv_sql, classz);
            if (StringUtils.isBlank(dim_key)) {
                System.err.println("没有满足聚合条件的key,sql_key:" + Constant.COUNT_UV);
                collector.ack(tuple);
                return;
            }
            //根据上报时间戳获取日期所在天的23:59:59 秒作为uid key 的 失效时间
            DateUtil dateUtil = new DateUtil();
            String timestamp = trackLog.getTimestamp();
            String long_time=dateUtil.timestamp2Date2(Long.valueOf(timestamp));
            long uid_expire_time =  dateUtil.Date2Timestamp(long_time+Constant.TIME_SUFFIX);

            String redis_key_head=Constant.COUNT_UV+"_";
            String day =  dateUtil.timestamp2Day(timestamp);
            //1. idfa
            String idfa = trackLog.getIdfa();
            createUV(idfa,uid_expire_time,redis_key_head+Constant.IDFA_+day,dim_key);
            String imei = trackLog.getImei();
            createUV(imei,uid_expire_time,redis_key_head+Constant.IMEI_+day,dim_key);
            String cid = trackLog.getCid();
            createUV(cid,uid_expire_time,redis_key_head+Constant.CID_+day,dim_key);


            if (tuple.getSourceComponent().equals(Constants.SYSTEM_COMPONENT_ID) &&
                    tuple.getSourceStreamId().equals(Constants.SYSTEM_TICK_STREAM_ID)){

            }
            collector.ack(tuple);

            return ;
        }catch (Exception e){
            e.printStackTrace();
            collector.ack(tuple);
            return ;
        }


    }

    /**
     * 分别 统计 独立用户数
     * @param uid
     * @param uid_expire_time
     * @param redis_key_head
     * @param dim_key
     */
    public void createUV(String uid,long uid_expire_time,String redis_key_head,String dim_key){
        if(StringUtils.isNotBlank(uid)){
            long sum_uid = jedisCluster.incr(uid);
            jedisCluster.expireAt(uid,uid_expire_time);
            if(sum_uid>1){
                System.err.println("此用户已经统计过,uid="+uid);
                return;
            }
            jedisCluster.hincrBy(redis_key_head,dim_key,1);
            jedisCluster.expire(redis_key_head,EXPIRE_TIME);
        }
        return ;
    }


    @Override
    public void cleanup() {
        System.out.println("UvCountBolt is close");
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("uvCount_message"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {

        Config conf = new Config();
        conf.put(conf.TOPOLOGY_TICK_TUPLE_FREQ_SECS, 10);
        return conf;
    }


}
