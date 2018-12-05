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
public class TrackingStatisticsChargeBolt extends AbstractTrackLog implements IRichBolt {
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
            String message = (String)tuple.getValue(4);
            //System.out.println("message:"+message);
            if(StringUtils.isBlank(message)){
                System.err.println("--kafka message error---:"+message);
                collector.ack(tuple);
                return ;
            }
            /**1.初始 track model*/
            AbstractTrackLog trackLog = super.build(message,super.sp_tuple);
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
            String id = trackLog.getId();
            String timestamp= trackLog.getTimestamp();
            //重置 id/time 防止上报错误数据过大,reids内存开销过大
            if(id.length()>30){
                trackLog.setId(id.substring(0,30));
            }
            if(timestamp.length()>13){
                trackLog.setTimestamp(timestamp.substring(0,13));
            }

            String ett= trackLog.getEtt();
            String appid = trackLog.getAppid();
            DateUtil dateUtil = new DateUtil();
            String day_hour =  dateUtil.timestamp2Hour(trackLog.getTimestamp());
            String hour="99";
            if(day_hour.length()==10){
                hour = day_hour.substring(8,10);
            }
            //System.out.println(EttEnum.NA);
            switch (ett){
                case "na":
                    jedisCluster.hincrBy(Constant.BI_TREND_NA_+day_hour,hour,1);
                    jedisCluster.expire(Constant.BI_TREND_NA_+day_hour,EXPIRE_TIME);
                    if(StringUtils.isNotBlank(appid)){
                        jedisCluster.hincrBy(Constant.BI_TREND_NA_PLAT+day_hour,"appid="+appid,1);
                        jedisCluster.expire(Constant.BI_TREND_NA_PLAT+day_hour,EXPIRE_TIME);
                    }
                    break;
                case "naa":
                    jedisCluster.hincrBy(Constant.BI_TREND_NAA_+day_hour,hour,1);
                    jedisCluster.expire(Constant.BI_TREND_NAA_+day_hour,EXPIRE_TIME);
                    if(StringUtils.isNotBlank(appid)){
                        jedisCluster.hincrBy(Constant.BI_TREND_NAA_PLAT+day_hour,"appid="+appid,1);
                        jedisCluster.expire(Constant.BI_TREND_NAA_PLAT+day_hour,EXPIRE_TIME);
                    }
                    break;
                case "v":
                    //System.out.println(EttEnum.V);
                    TrackTrendMonitor v_no_charge= new TrackTrendMonitor(jedisCluster,trackLog);
                    v_no_charge.execute();
                    break;
                case "av":
                    //System.out.println(EttEnum.AV);
                    TrackTrendMonitor av_no_charge= new TrackTrendMonitor(jedisCluster,trackLog);
                    av_no_charge.execute();
                    break;
                case "click":
                   // System.out.println(EttEnum.CLICK);
                    TrackTrendMonitor c_no_charge = new TrackTrendMonitor(jedisCluster,trackLog);
                    c_no_charge.execute();
                    break;

                default:
                    System.out.println("此类型日志,暂不出处理,ett="+ett);
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
        System.out.println("TrackingNoChargeBolt is close");
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
