package com.li.storm.bolt.sstics;

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
import redis.clients.jedis.JedisCluster;

import java.util.Map;

/**
 * Created by wangdi on 17/7/6. IRichBolt
 */
@Deprecated
public class StatisticsAVBolt extends AbstractTrackLog implements IRichBolt {
    private static final long serialVersionUID = 3973354285192118060L;
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
        try{
            String message = (String)tuple.getValue(0);
            System.out.println("message:"+message);
            if(StringUtils.isBlank(message)){
                System.err.println("--kafka message error---:"+message);
                collector.ack(tuple);
                return ;
            }
            /**1.初始 track model*/
            AbstractTrackLog trackLog = super.build(message,super.sp_tuple);
            String id = trackLog.getId();
            String timestamp = trackLog.getTimestamp();
            /**过滤作弊数据*/
            if (super.checkIsSpam(trackLog)){
                collector.ack(tuple);
                return ;
            }
            /**过滤错误数据*/
            if(!checkBusinessData(trackLog)){
                collector.ack(tuple);
                return ;
            }
            /**过滤重复上报数据*/
            StringBuilder sb = new StringBuilder(Constant.BI_SSTICS_);
            String repeat_key = sb.append("AV_").append(id).append("_").append(timestamp).toString();
            long count =  jedisCluster.incr(repeat_key);
            jedisCluster.expire(repeat_key,60);
            if(count>1){
                System.err.println("重复数据,不做业务处理. "+repeat_key);
                collector.ack(tuple);
                return ;
            }

            DateUtil dateUtil = new DateUtil();
            String day =  dateUtil.timestamp2Day(timestamp);
            String day_hour =  dateUtil.timestamp2Hour(timestamp);
            String hour="99";
            if(day_hour.length()==10){
                hour = day_hour.substring(8,10);
            }
            /**2.统计加载次数**/
            jedisCluster.hincrBy(Constant.BI_SSTICS_AV_ALL_+day,hour,1);
            jedisCluster.expire(Constant.BI_SSTICS_AV_ALL_+day,EXPIRE_TIME);
            /**过滤区分品牌错误数据**/
            String adgres = trackLog.getAdgres();
            if(StringUtils.isBlank(adgres)){
                System.err.println("adgres is null,c_id="+trackLog.getId());
                collector.ack(tuple);
                return;
            }
            /**3区分品牌中长尾**/
            if("1".equals(trackLog.getAdgres())){
                //品牌点击量
                jedisCluster.hincrBy(Constant.BI_SSTICS_AV_+day,Constant.BRAND_COUNT_+hour,1);
                jedisCluster.expire(Constant.BI_SSTICS_AV_+day,EXPIRE_TIME);

            }
            if("2".equals(trackLog.getAdgres())||"4".equals(trackLog.getAdgres())){
                /**中长尾点击量**/
                jedisCluster.hincrBy(Constant.BI_SSTICS_AV_+day,Constant.SURPLUS_COUNT_+hour,1);
                jedisCluster.expire(Constant.BI_SSTICS_AV_+day,EXPIRE_TIME);

            }

            /**4.计算收入  cpm**/
            if(checkIncome(trackLog)&&"4".equals(trackLog.getBidtype())){
                if("1".equals(trackLog.getAdgres())){
                    // 品牌
                    jedisCluster.hincrBy(Constant.BI_SSTICS_BRAND_CONSUME_+day,Constant.AV_BRAND_+hour,Long.valueOf(trackLog.getCharge()));
                    jedisCluster.expire(Constant.BI_SSTICS_BRAND_CONSUME_+day,EXPIRE_TIME);
                    //cpc 统计品牌收入计算次数
                    jedisCluster.hincrBy(Constant.BI_SSTICS_DCPM_+day,Constant.AV_BRAND_+hour,1);
                    jedisCluster.expire(Constant.BI_SSTICS_DCPM_+day,EXPIRE_TIME);
                }

                if("2".equals(trackLog.getAdgres())||"4".equals(trackLog.getAdgres())){
                    jedisCluster.hincrBy(Constant.BI_SSTICS_SURPLUS_CONSUME_+day,Constant.AV_SURPLUS_+hour,Long.valueOf(trackLog.getCharge()));
                    jedisCluster.expire(Constant.BI_SSTICS_SURPLUS_CONSUME_+day,EXPIRE_TIME);
                    //cpc 统计中长尾收入计算次数
                    jedisCluster.hincrBy(Constant.BI_SSTICS_DCPM_+day,Constant.AV_SURPLUS_+hour,1);
                    jedisCluster.expire(Constant.BI_SSTICS_DCPM_+day,EXPIRE_TIME);
                }


            }
            collector.ack(tuple);
            return ;

        }catch (Exception e){
            System.err.println("业务处理异常");
            e.printStackTrace();
            collector.ack(tuple);
            return ;

        }



    }

    @Override
    public void cleanup() {
        System.out.println("StatisticsClickBolt is close");
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
