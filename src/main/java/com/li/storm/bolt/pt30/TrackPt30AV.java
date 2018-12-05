package com.li.storm.bolt.pt30;

import com.li.core.AbstractTrackLog;
import com.li.redis.Constant;
import org.apache.commons.lang.StringUtils;
import redis.clients.jedis.JedisCluster;

import java.io.Serializable;

/**
 * Created by wangdi on 18/1/3.
 * av 计费
 */
public class TrackPt30AV implements Serializable {

    private static final long serialVersionUID = -1851967405016857749L;
    private  JedisCluster jedisCluster;
    private AbstractTrackLog trackLog;
    private static final int EXPIRE_TIME = 24*60*60;

    public TrackPt30AV(JedisCluster jedisCluster, AbstractTrackLog trackLog){
        this.jedisCluster = jedisCluster;
        this.trackLog = trackLog;
    }


    public void execute(String r_table,String daytime,String hourtime,String dimkey){

        if(StringUtils.isNotBlank(trackLog.getAc())&&!"1".equals(trackLog.getAc())){
            System.out.println("不计费,appid="+trackLog.getAppid()+",ac="+trackLog.getAc());
            return;
        }
        /**曝光操作处理**/
        //加载
        if(trackLog.getBidtype().equals("4")&& StringUtils.isNotBlank(trackLog.getCharge())){
            //消耗量累加
            jedisCluster.hincrBy(r_table+"_"+Constant.CONSUME_+hourtime,dimkey,Long.valueOf(trackLog.getCharge()));
            jedisCluster.expire(r_table+"_"+Constant.CONSUME_+hourtime,EXPIRE_TIME);

            //展示量++
            jedisCluster.hincrBy(r_table+"_"+Constant.V_+hourtime,dimkey,1);
            jedisCluster.expire(r_table+"_"+Constant.V_+hourtime,EXPIRE_TIME);
        }
        if(trackLog.getBidtype().equals("3")){
            //展示量++
            jedisCluster.hincrBy(r_table+"_"+Constant.V_+hourtime,dimkey,1);
            jedisCluster.expire(r_table+"_"+Constant.V_+hourtime,EXPIRE_TIME);
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
