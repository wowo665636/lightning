package com.li.storm.bolt.charge;

import com.li.core.AbstractTrackLog;
import com.li.redis.Constant;
import com.li.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import redis.clients.jedis.JedisCluster;

import java.io.Serializable;

/**
 * Created by wangdi on 18/1/3.
 * av 计费
 */
public class TrackAVCharge implements Serializable {
    private static final long serialVersionUID = -6674237065890339917L;
    private  JedisCluster jedisCluster;
    private AbstractTrackLog trackLog;
    private static final int EXPIRE_TIME = 24*60*60;

    public TrackAVCharge(JedisCluster jedisCluster, AbstractTrackLog trackLog){
        this.jedisCluster = jedisCluster;
        this.trackLog = trackLog;
    }


    public void execute(){
        String type_vid = "av_id";//

        DateUtil dateUtil = new DateUtil();
        final String tt3_hour_value = dateUtil.timestamp2Hour(trackLog.getTimestamp());
        final String hour_field_key = convertFieldKey(trackLog.getAid(),trackLog.getCampid(),trackLog.getAdgid(),trackLog.getCrid());
        /**过滤重复数据**/
        /*TrackCheckUtil checkUtil = new  TrackCheckUtil(jedisCluster);
        if(checkUtil.checkRepeat(type_vid,trackLog.getId(),trackLog.getTimestamp())){
            System.err.println("-- id repetition. type_vid="+trackLog.getId()+",time="+trackLog.getTimestamp());
            checkUtil.setRepeatkey(Constant.TC_REPEAT_AV_KEY+tt3_hour_value,trackLog.getId(),EXPIRE_TIME);
            return ;
        }*/

        if(StringUtils.isNotBlank(trackLog.getAc())&&!"1".equals(trackLog.getAc())){
            System.out.println("不计费,appid="+trackLog.getAppid()+",ac="+trackLog.getAc());
            return;
        }
        /**曝光操作处理**/
        //加载
        if(trackLog.getBidtype().equals("4")&& StringUtils.isNotBlank(trackLog.getCharge())){
            //消耗量累加
            String consumeKey=consumeKey(tt3_hour_value);
            String impkey = impkey(tt3_hour_value);
            incrConsumeAndImp(consumeKey,impkey,hour_field_key,trackLog.getCharge());
        }
        if(trackLog.getBidtype().equals("3")){
            String impkey = impkey(tt3_hour_value);
            //展示量++
            jedisCluster.hincrBy(impkey,hour_field_key,1);
            jedisCluster.expire(impkey,EXPIRE_TIME);
            System.out.println("imp_incr:"+impkey+", "+hour_field_key);
        }
        return;
    }

    public void incrConsumeAndImp(String consumeKey,String impkey ,String hour_field_key,String  charge){
        jedisCluster.hincrBy(consumeKey,hour_field_key,Long.valueOf(charge));
        jedisCluster.expire(consumeKey,EXPIRE_TIME);
        System.out.println("consume_incr:"+consumeKey+", "+hour_field_key);
        //展示量++
        jedisCluster.hincrBy(impkey,hour_field_key,1);
        jedisCluster.expire(impkey,EXPIRE_TIME);
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
        return Constant.aid+aid+"-"+Constant.campid+campid+"-"+Constant.adgid+adgid+"-"+Constant.crid+crid;
    }

    public JedisCluster getJedisCluster() {
        return jedisCluster;
    }

    public void setJedisCluster(JedisCluster jedisCluster) {
        this.jedisCluster = jedisCluster;
    }

    public AbstractTrackLog getTrackLog() {
        return trackLog;
    }

    public void setTrackLog(AbstractTrackLog trackLog) {
        this.trackLog = trackLog;
    }
}
