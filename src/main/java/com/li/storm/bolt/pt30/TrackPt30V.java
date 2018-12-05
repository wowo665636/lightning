package com.li.storm.bolt.pt30;

import com.li.core.AbstractTrackLog;
import com.li.redis.Constant;
import org.apache.commons.lang.StringUtils;
import redis.clients.jedis.JedisCluster;

import java.io.Serializable;

/**
 * Created by wangdi on 18/1/3.
 * v 计费
 */
public class TrackPt30V implements Serializable {
    private static final long serialVersionUID = -7420096115809463795L;
    private  JedisCluster jedisCluster;
    private AbstractTrackLog trackLog;
    private static final int EXPIRE_TIME = 24*60*60;

    public TrackPt30V(JedisCluster jedisCluster, AbstractTrackLog trackLog){
        this.jedisCluster = jedisCluster;
        this.trackLog = trackLog;
    }


    public void execute(String r_table,String daytime,String hourtime,String dimkey){

        if("squirrel".equals(trackLog.getAppid())){
            if(StringUtils.isNotBlank(trackLog.getAc())&&!"1".equals(trackLog.getAc())){
                System.out.println("资讯版:不计费,appid="+trackLog.getAppid()+",ac="+trackLog.getAc());
                return;
            }

        }
        if(trackLog.getBidtype().equals("2")&& StringUtils.isNotBlank(trackLog.getCharge())){
            //消耗量累加
            jedisCluster.hincrBy(r_table+"_"+Constant.CONSUME_+hourtime,dimkey,Long.valueOf(trackLog.getCharge()));
            jedisCluster.expire(r_table+"_"+Constant.CONSUME_+hourtime,EXPIRE_TIME);
            //展示量++
            jedisCluster.hincrBy(r_table+"_"+Constant.V_+hourtime,dimkey,1);
            jedisCluster.expire(r_table+"_"+Constant.V_+hourtime,EXPIRE_TIME);

        }
        if(trackLog.getBidtype().equals("1")){
            //展示量++
            jedisCluster.hincrBy(r_table+"_"+Constant.V_+hourtime,dimkey,1);
            jedisCluster.expire(r_table+"_"+Constant.V_+hourtime,EXPIRE_TIME);

        }
        return;
    }


    /*public String consumeKey(String time){
        return Constant.BI+Constant.consume_amount+time;
    }

    public String impkey(String time){
        return Constant.BI+Constant.imp_amount+time;
    }
    public String convertFieldKey(String aid,String campid,String adgid,String crid){
        return Constant.aid+aid+"-"+Constant.campid+campid+"-"+Constant.adgid+adgid+"-"+Constant.crid+crid;
    }*/


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
