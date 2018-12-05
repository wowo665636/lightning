package com.li.storm.bolt.pt30;

import com.li.core.AbstractTrackLog;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisCluster;

import java.io.IOException;
import java.util.Map;

/**
 * Created by wangdi on 18/10/24.
 */
public class TrackPt30Bolt extends AbstractTrackLog implements IRichBolt {
    private static final Logger logger = LoggerFactory.getLogger(TrackPt30Bolt.class);
    private static final long serialVersionUID = -4521622320449452926L;
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
            if(StringUtils.isBlank(message)){
                collector.ack(tuple);
                return ;
            }
            /**1.初始 track model*/
            AbstractTrackLog trackLog = super.build(message,super.sp_tuple);
            String ett = trackLog.getEtt();
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
            /**过滤非计费数据**/
            if(!checkIncome(trackLog)){
                collector.ack(tuple);
                return ;
            }
            String timestamp = trackLog.getTimestamp();
            DateUtil dateUtil = new DateUtil();
            String day_key = dateUtil.timestamp2Day(timestamp);
            String hour_key  =  dateUtil.timestamp2Hour(timestamp);

            Map<String, String>  dim_map = jedisCluster.hgetAll(Constant.BI_DIM_ADSERVICE_RULE);
            if(dim_map==null){
                logger.error("尚未定制维度聚合模板,请配置sql模板");
                collector.ack(tuple);
                return ;
            }
            //遍历所有数据模板
            for(Map.Entry<String,String> entry:dim_map.entrySet()){
                String r_table = Constant.BI_ADSERVICE_+entry.getKey();
                try{
                    CustomizationMonitor customization  = new CustomizationMonitor(jedisCluster,trackLog);
                    String dim_key = customization.execute(r_table,entry.getValue(),trackLog.getClass());
                    if(StringUtils.isNotBlank(dim_key)){

                        switch (ett){
                            case "v":
                                //System.out.println(EttEnum.V+"_message:"+message);
                                TrackPt30V v_exe= new TrackPt30V(jedisCluster,trackLog);
                                v_exe.execute(r_table,day_key,hour_key,dim_key);
                                break;
                            case "av":
                                TrackPt30AV av_exe= new TrackPt30AV(jedisCluster,trackLog);
                                av_exe.execute(r_table,day_key,hour_key,dim_key);
                                break;
                            case "click":
                                TrackPt30C c_exe= new TrackPt30C(jedisCluster,trackLog);
                                c_exe.execute(r_table,day_key,hour_key,dim_key);
                                break;
                            default:
                                System.out.println("其他类型,不做计费处理,ett="+ett);
                        }

                    }

                }catch (Exception e){
                    logger.error(r_table+" is exception:",e);
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
        System.out.println("TrackingChargeBolt is close");
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
