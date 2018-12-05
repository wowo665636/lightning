package com.li.storm.bolt.charge;

import com.li.common.TrackCheckUtil;
import com.li.core.AbstractTrackLog;
import com.li.redis.Constant;
import com.li.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import redis.clients.jedis.JedisCluster;

import java.io.Serializable;

/**
 * Created by wangdi on 18/1/3.
 * c 计费
 */
public class TrackCCharge implements Serializable {
    private static final long serialVersionUID = -1382548077670831555L;
    private  JedisCluster jedisCluster;
    private AbstractTrackLog trackLog;
    private static final int EXPIRE_TIME = 24*60*60;

    public TrackCCharge(JedisCluster jedisCluster, AbstractTrackLog trackLog){
        this.jedisCluster = jedisCluster;
        this.trackLog = trackLog;
    }


    public void execute(){
        String type_vid = "c_id";//
        DateUtil dateUtil = new DateUtil();
        final String tt3_hour_value = dateUtil.timestamp2Hour(trackLog.getTimestamp());
        final String hour_key = convertKey(type_vid,tt3_hour_value);
        final String hour_field_key = convertFieldKey(trackLog.getAid(),trackLog.getCampid(),trackLog.getAdgid(),trackLog.getCrid());

        /**重复上报验证**/
        /*TrackCheckUtil checkUtil = new  TrackCheckUtil(jedisCluster);
        if(checkUtil.checkRepeat(type_vid,trackLog.getId(),trackLog.getTimestamp())){
            System.err.println("-- id repetition. type_vid="+trackLog.getId()+",time="+trackLog.getTimestamp());
            checkUtil.setRepeatkey(Constant.TC_REPEAT_V_KEY+tt3_hour_value,trackLog.getId(),EXPIRE_TIME);
            return ;
        }*/
        /**1.点击操作处理**/
        jedisCluster.hincrBy(hour_key,hour_field_key,1);
        jedisCluster.expire(hour_key,EXPIRE_TIME);
        System.out.println("click_incr:"+hour_key+", "+hour_field_key);
        //1 按天计费 cpd  ;2 cpm  每千次加载 ; 3  cpc每次 点击  ;4 dcpm  千次曝光
        /**2.c_id 更新消耗量,步进值 charge**/
        if("3".equals(trackLog.getBidtype())&& StringUtils.isNotBlank(trackLog.getCharge())){
            String consumeKey=consumeKey(tt3_hour_value);
            jedisCluster.hincrBy(consumeKey,hour_field_key,Long.valueOf(trackLog.getCharge()));
            System.out.println("click_consume_incr:"+consumeKey+", "+hour_field_key);
            jedisCluster.expire(consumeKey,EXPIRE_TIME);
        }

        return;
    }


    public String convertKey(String type,String time){
        return Constant.BI+type+"_"+time;
    }

    public String consumeKey(String time){
        return Constant.BI+Constant.consume_amount+time;
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
