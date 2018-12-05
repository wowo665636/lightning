package com.li.storm.bolt.charge;

import com.li.core.AbstractTrackLog;
import com.li.redis.cluster.RedisClusterHandler;
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
public class TrackingChargeBolt extends AbstractTrackLog implements IRichBolt {
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
            //System.out.println("--message:"+message);
            if(StringUtils.isBlank(message)){
              //  System.err.println("--kafka message error---:"+message);
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

           switch (ett){
                case "v":
                    //System.out.println(EttEnum.V+"_message:"+message);
                    TrackVCharge v_charge= new TrackVCharge(jedisCluster,trackLog);
                    v_charge.execute();
                    break;
                case "av":
                    //System.out.println(EttEnum.AV+"_message:"+message);
                    TrackAVCharge av_charge= new TrackAVCharge(jedisCluster,trackLog);
                    av_charge.execute();
                    break;
                case "click":
                    //System.out.println(EttEnum.CLICK+"_message:"+message);
                    TrackCCharge c_charge = new TrackCCharge(jedisCluster,trackLog);
                    c_charge.execute();
                    break;
                default:
                    System.out.println("其他类型,不做计费处理,ett="+ett);
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

    public OutputCollector getCollector() {
        return collector;
    }

    public void setCollector(OutputCollector collector) {
        this.collector = collector;
    }
}
