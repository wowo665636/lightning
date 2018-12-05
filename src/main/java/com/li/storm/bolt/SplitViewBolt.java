package com.li.storm.bolt;

import com.li.common.TrackCheckUtil;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wangdi on 17/7/6. IRichBolt
 */
@Deprecated
public class SplitViewBolt implements IRichBolt {
    private static final long serialVersionUID = -5097093020986900304L;
    //private static Logger log = LoggerFactory.getLogger(SplitBolt.class);

    private OutputCollector collector;

    private static final Pattern p_v= Pattern.compile("^id=(.*?)\t");// 新日志  id
    //private static final Pattern p_tt3= Pattern.compile("\ttt3=(.*?)\t");
    private static final Pattern p_timestamp= Pattern.compile("\ttimestamp=([0-9]{13})");
    private static final Pattern p_id= Pattern.compile("(aid=.*?)\t(campid=.*?)\t(adgid=.*?)\t(crid=.*?)\t");
    private static final Pattern p_bidtype= Pattern.compile("\tbidtype=(.*?)\t");
    private static final Pattern p_charge= Pattern.compile("\tcharge=(.*?)\t");
    private static final Pattern p_status= Pattern.compile("\tstatus=(.*?)\t");
    private static final Pattern p_isspam= Pattern.compile("\tisspam=(.*?)\t");

    private static final int EXPIRE_TIME = 24*60*60;
    private static JedisCluster  jedisCluster;


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
        String word = (String) tuple.getValue(0);
        String type_vid = "v_id";//
         String vid_value = "";//操作
        String time = "";
        String filedKey="";
        String bidtype =""; //业务类型
        String charge="";
        String status="";
        String isspam = "";
        if(StringUtils.isBlank(word)){
            System.err.println("--kafka message error---:"+word);
            collector.ack(tuple);
            return ;
        }
        Matcher vid_value_matcher =  p_v.matcher(word);
        if(vid_value_matcher.find()){
            vid_value = vid_value_matcher.group(1);
        }

        Matcher timestamp_matcher =  p_timestamp.matcher(word);
        if(timestamp_matcher.find()){
            time = timestamp_matcher.group(1);
        }
        Matcher p_id_matcher =  p_id.matcher(word);
        if(p_id_matcher.find()){
            filedKey=convertFieldKey(p_id_matcher.group(1),p_id_matcher.group(2),p_id_matcher.group(3),p_id_matcher.group(4));
        }

        Matcher p_bidtype_matcher =  p_bidtype.matcher(word);
        if(p_bidtype_matcher.find()){
            bidtype = p_bidtype_matcher.group(1);
        }

        Matcher p_charge_matcher =  p_charge.matcher(word);
        if(p_charge_matcher.find()){
            charge = p_charge_matcher.group(1);
        }

        Matcher p_status_matcher =  p_status.matcher(word);
        if(p_status_matcher.find()){
            status = p_status_matcher.group(1);
        }
        Matcher p_isspam_matcher =  p_isspam.matcher(word);
        if(p_isspam_matcher.find()){
            isspam = p_isspam_matcher.group(1);
        }
        TrackCheckUtil checkUtil = new  TrackCheckUtil(jedisCluster);
        DateUtil dateUtil = new DateUtil();
        final String tt3_hour_value = dateUtil.timestamp2Hour(time);
        final String hour_key = convertKey(type_vid,tt3_hour_value);
        final String hour_field_key = filedKey;
        /**step.1 log 日志业务数据非空验证*/
        boolean checkData = checkUtil.checkBusinessData(type_vid, vid_value, time, status, isspam, bidtype);
        if(!checkData){
            System.err.println("--checkBusinessData error---:type_vid="+type_vid+",id="+vid_value);
            collector.ack(tuple);
            return ;
        }
        /**step.2 根据log_id 过滤重复数据*/
        if(checkUtil.checkRepeat(type_vid,vid_value,time)){
            System.err.println("-- id repetition. type_vid="+vid_value+",time="+time);
            checkUtil.setRepeatkey(Constant.TC_REPEAT_V_KEY+tt3_hour_value,vid_value,EXPIRE_TIME);
            collector.ack(tuple);
            return ;
        }
        //写入redis 1499312039
        System.out.println("####info_time="+time+",type_vid="+type_vid+",hour_key="+hour_key+",hour_field_key="+hour_field_key);
        try{
            String type_count = type_vid+"_"+dateUtil.timestamp2Day(time);//记录每天的每种类型数量redis 统计
            jedisCluster.incr(type_count);
            jedisCluster.expire(type_count,EXPIRE_TIME);
            /**加载操作处理*/
            if(bidtype.trim().equals("2")&& StringUtils.isNotBlank(charge)){
                //消耗量累加
                String consumeKey=consumeKey(tt3_hour_value);
                String impkey = impkey(tt3_hour_value);
                jedisCluster.hincrBy(consumeKey,hour_field_key,Long.valueOf(charge));
                jedisCluster.expire(consumeKey,EXPIRE_TIME);
                System.out.println("consume_incr:"+consumeKey+", "+hour_field_key);
                //展示量++
                jedisCluster.hincrBy(impkey,hour_field_key,1);
                jedisCluster.expire(impkey,EXPIRE_TIME);
                System.out.println("imp_incr:"+impkey+", "+hour_field_key);

            }
            if(bidtype.trim().equals("1")){
                String impkey = impkey(tt3_hour_value);
                //展示量++
                jedisCluster.hincrBy(impkey,hour_field_key,1);
                jedisCluster.expire(impkey,EXPIRE_TIME);
                System.out.println("imp_incr:"+impkey+", "+hour_field_key);
            }

        }catch (Exception e){
            System.err.println("redis write is error,hour_key="+hour_key+",hour_field_key="+hour_field_key+e.getMessage() );
        }
        collector.ack(tuple);
    }


    @Override
    public void cleanup() {
        System.out.println("topo is close");
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("message"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }


    public String convertKey(String type,String time){
        return Constant.BI+type+"_"+time;
    }

    public String consumeKey(String time){
        return Constant.BI+Constant.consume_amount+time;
    }

    public String impkey(String time){
        return Constant.BI+Constant.imp_amount+time;
    }
    public String convertFieldKey(String aid,String campid,String adgid,String crid){
        return aid+"-"+campid+"-"+adgid+"-"+crid;
    }





    public OutputCollector getCollector() {
        return collector;
    }

    public void setCollector(OutputCollector collector) {
        this.collector = collector;
    }

}
