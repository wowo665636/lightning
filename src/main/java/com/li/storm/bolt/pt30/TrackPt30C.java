package com.li.storm.bolt.pt30;

import com.li.core.AbstractTrackLog;
import com.li.redis.Constant;
import org.apache.commons.lang.StringUtils;
import redis.clients.jedis.JedisCluster;

import java.io.Serializable;

/**
 * Created by wangdi on 18/1/3.
 * c 计费
 */
public class TrackPt30C implements Serializable {
    private static final long serialVersionUID = 3660800824461407528L;
    private  JedisCluster jedisCluster;
    private AbstractTrackLog trackLog;
    private static final int EXPIRE_TIME = 24*60*60;

    public TrackPt30C(JedisCluster jedisCluster, AbstractTrackLog trackLog){
        this.jedisCluster = jedisCluster;
        this.trackLog = trackLog;
    }


    public void execute(String r_table,String daytime,String hourtime,String dimkey){

        /**1.点击操作处理**/
        jedisCluster.hincrBy(r_table+"_"+Constant.C_+hourtime,dimkey,1);
        jedisCluster.expire(r_table+"_"+Constant.C_+hourtime,EXPIRE_TIME);
        //1 按天计费 cpd  ;2 cpm  每千次加载 ; 3  cpc每次 点击  ;4 dcpm  千次曝光
        /**2.c_id 更新消耗量,步进值 charge**/
        if("3".equals(trackLog.getBidtype())&& StringUtils.isNotBlank(trackLog.getCharge())){
            jedisCluster.hincrBy(r_table+"_"+Constant.CONSUME_+hourtime,dimkey,Long.valueOf(trackLog.getCharge()));
            jedisCluster.expire(r_table+"_"+Constant.CONSUME_+hourtime,EXPIRE_TIME);
        }

        return;
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
