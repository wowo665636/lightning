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
 * v 计费
 */
public class TrackVCharge implements Serializable {
    private static final long serialVersionUID = -7420096115809463795L;
    private  JedisCluster jedisCluster;
    private AbstractTrackLog trackLog;
    private static final int EXPIRE_TIME = 24*60*60;

    public TrackVCharge(JedisCluster jedisCluster,AbstractTrackLog trackLog){
        this.jedisCluster = jedisCluster;
        this.trackLog = trackLog;
    }


    public void execute(){
        DateUtil dateUtil = new DateUtil();
        final String type_vid = "v_id";

        final String tt3_hour_value = dateUtil.timestamp2Hour(trackLog.getTimestamp());
        // final String hour_key = convertKey(type_vid,tt3_hour_value);
        final String hour_field_key = convertFieldKey(trackLog.getAid(),trackLog.getCampid(),trackLog.getAdgid(),trackLog.getCrid());
        /**重复上报验证**/
        /*TrackCheckUtil checkUtil = new  TrackCheckUtil(jedisCluster);
        if(checkUtil.checkRepeat(type_vid,trackLog.getId(),trackLog.getTimestamp())){
            System.err.println("-- id repetition. type_vid="+trackLog.getId()+",time="+trackLog.getTimestamp());
            checkUtil.setRepeatkey(Constant.TC_REPEAT_V_KEY+tt3_hour_value,trackLog.getId(),EXPIRE_TIME);
            return ;
        }*/

        if("squirrel".equals(trackLog.getAppid())){
            if(StringUtils.isNotBlank(trackLog.getAc())&&!"1".equals(trackLog.getAc())){
                System.out.println("资讯版:不计费,appid="+trackLog.getAppid()+",ac="+trackLog.getAc());
                return;
            }

        }

        if(trackLog.getBidtype().equals("2")&& StringUtils.isNotBlank(trackLog.getCharge())){
            //消耗量累加
            String consumeKey=consumeKey(tt3_hour_value);
            String impkey = impkey(tt3_hour_value);
            jedisCluster.hincrBy(consumeKey,hour_field_key,Long.valueOf(trackLog.getCharge()));
            jedisCluster.expire(consumeKey,EXPIRE_TIME);
            //System.out.println("consume_incr:"+consumeKey+", "+hour_field_key);
            //展示量++
            jedisCluster.hincrBy(impkey,hour_field_key,1);
            jedisCluster.expire(impkey,EXPIRE_TIME);
            // System.out.println("imp_incr:"+impkey+", "+hour_field_key);

        }
        if(trackLog.getBidtype().equals("1")){
            String impkey = impkey(tt3_hour_value);
            //展示量++
            jedisCluster.hincrBy(impkey,hour_field_key,1);
            jedisCluster.expire(impkey,EXPIRE_TIME);
            //System.out.println("imp_incr:"+impkey+", "+hour_field_key);
        }

        return;
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
