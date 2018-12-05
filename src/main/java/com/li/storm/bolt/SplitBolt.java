package com.li.storm.bolt;

import com.li.redis.Constant;
import com.li.redis.connect.RedisClient;
import com.li.redis.connect.RedisHandler;
import com.li.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wangdi on 17/7/6. IRichBolt
 */
@Deprecated
public class SplitBolt implements IRichBolt {
    //private static Logger log = LoggerFactory.getLogger(SplitBolt.class);
    private static final long serialVersionUID = -4964927682984507558L;
    private OutputCollector collector;

    private static final Pattern p_v= Pattern.compile("^(c_id|v_id|av_id)=.*?\t");// 新日志  id ,isspam
    //private static final Pattern p_tt3= Pattern.compile("\ttt3=(.*?)\t");
    private static final Pattern p_tt3= Pattern.compile("\ttimestamp=([0-9]{13})");
    private static final Pattern p_id= Pattern.compile("(aid=.*?)\t(campid=.*?)\t(adgid=.*?)\t(crid=.*?)\t");
    private static final Pattern p_bidtype= Pattern.compile("\tbidtype=(.*?)\t");
    private static final Pattern p_charge= Pattern.compile("\tcharge=(.*?)\t");
    private static final Pattern p_status= Pattern.compile("\tstatus=(.*?)\t");
    private static final int EXPIRE_TIME = 24*60*60;
    private static RedisClient jedis;


    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector = outputCollector;
        try{
            //redis 连接池
            this.jedis = RedisHandler.getInstance().get(Constant.REDIS_POOL_22149);
        }catch (Exception e){
            System.err.println("jedis is null");
        }


    }

    @Override
    public void execute(Tuple tuple) {
        String word = (String) tuple.getValue(0);
       // String uid = UUID.randomUUID().toString();
        String type_vid = "";//操作
        String time = "";
        String filedKey="";
        String bidtype =""; //业务类型
        String charge="";
        String status="";
       // jedis.incr("kafka_offset");
       // jedis.expire("kafka_offset",EXPIRE_TIME);
        System.out.println("@:"+word);
        if(StringUtils.isBlank(word)){
            System.err.println("--kafka message error---:"+word);
            collector.ack(tuple);
            return ;
        }
        Matcher vid_matcher =  p_v.matcher(word);
        if(vid_matcher.find()){
            type_vid = vid_matcher.group(1);
        }
        Matcher tt3_matcher =  p_tt3.matcher(word);
        if(tt3_matcher.find()){
            time = tt3_matcher.group(1);
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
        //1 按天计费 cpd  ;2 cpm  每千次加载 ; 3  cpc每次 点击  ;4 dcpm  千次曝光
        if(StringUtils.isBlank(type_vid)||StringUtils.isBlank(time)){
            System.err.println("--time is error--type_vid="+type_vid+",time="+time);
            collector.ack(tuple);
            return;
        }
        //写入redis 1499312039
        DateUtil dateUtil = new DateUtil();
        final String tt3_hour_value = dateUtil.timestamp2Hour(time);
        final String hour_key = convertKey(type_vid,tt3_hour_value);
        final String hour_field_key = filedKey;
        System.out.println("####info_time="+time+",type_vid="+type_vid+",hour_key="+hour_key+",hour_field_key="+hour_field_key);
        try{
            /**点击操作处理**/
            if(Constant.c_id.equals(type_vid.trim())){
                //点击量
                jedis.hincrBy(hour_key,hour_field_key,1);
                jedis.expire(hour_key,EXPIRE_TIME);
                System.out.println("click_incr:"+hour_key+", "+hour_field_key);
                if(StringUtils.isBlank(bidtype)){
                    System.err.println("--bidtype is null,type_vid="+type_vid+",time="+time+",only ++ click");
                    collector.ack(tuple);
                    return;
                }
                /**c_id 更新消耗量,步进值 charge**/
                if("3".equals(bidtype.trim())&& StringUtils.isNotBlank(charge)){
                    String consumeKey=consumeKey(tt3_hour_value);
                    jedis.hincrBy(consumeKey,hour_field_key,Long.valueOf(charge));
                    System.out.println("consume_incr:"+consumeKey+", "+hour_field_key);
                    jedis.expire(consumeKey,EXPIRE_TIME);
                }
            }
            /**加载操作处理*/
            if(Constant.v_id.equals(type_vid)){
                //加载
                if(StringUtils.isBlank(bidtype)){
                    System.err.println("--bidtype is null,type_vid="+type_vid+",time="+time);
                    collector.ack(tuple);
                    return;
                }
                if(bidtype.trim().equals("2")&& StringUtils.isNotBlank(charge)){
                    //消耗量累加
                    String consumeKey=consumeKey(tt3_hour_value);
                    String impkey = impkey(tt3_hour_value);
                    jedis.hincrBy(consumeKey,hour_field_key,Long.valueOf(charge));
                    jedis.expire(consumeKey,EXPIRE_TIME);
                    System.out.println("consume_incr:"+consumeKey+", "+hour_field_key);
                    if(!"0".equals(status)){
                        //展示量++
                        jedis.hincrBy(impkey,hour_field_key,1);
                        jedis.expire(impkey,EXPIRE_TIME);
                        System.out.println("imp_incr:"+impkey+", "+hour_field_key);
                    }

                }
                if(bidtype.trim().equals("1")&&!"0".equals(status)){
                    String impkey = impkey(tt3_hour_value);
                    //展示量++
                    jedis.hincrBy(impkey,hour_field_key,1);
                    jedis.expire(impkey,EXPIRE_TIME);
                    System.out.println("imp_incr:"+impkey+", "+hour_field_key);
                }


            }
            /**曝光操作处理**/
            if(Constant.av_id.equals(type_vid)){
                //加载
                if(StringUtils.isBlank(bidtype)){
                    System.err.println("--bidtype is null,type_vid="+type_vid+",time="+time);
                    collector.ack(tuple);
                    return;
                }
                if(bidtype.trim().equals("4")&& StringUtils.isNotBlank(charge)){
                    //消耗量累加
                    String consumeKey=consumeKey(tt3_hour_value);
                    String impkey = impkey(tt3_hour_value);
                    incrConsumeAndImp(consumeKey,impkey,hour_field_key,charge);
                }
                if(bidtype.trim().equals("3")){
                    String impkey = impkey(tt3_hour_value);
                    //展示量++
                    jedis.hincrBy(impkey,hour_field_key,1);
                    jedis.expire(impkey,EXPIRE_TIME);
                    System.out.println("imp_incr:"+impkey+", "+hour_field_key);
                }


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

    public void incrConsumeAndImp(String consumeKey,String impkey ,String hour_field_key,String  charge){
        jedis.hincrBy(consumeKey,hour_field_key,Long.valueOf(charge));
        jedis.expire(consumeKey,EXPIRE_TIME);
        System.out.println("consume_incr:"+consumeKey+", "+hour_field_key);
        //展示量++
        jedis.hincrBy(impkey,hour_field_key,1);
        jedis.expire(impkey,EXPIRE_TIME);
        System.out.println("imp_incr:"+impkey+", "+hour_field_key);
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
