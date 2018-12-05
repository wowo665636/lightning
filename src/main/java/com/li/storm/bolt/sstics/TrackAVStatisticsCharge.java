package com.li.storm.bolt.sstics;

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
public class TrackAVStatisticsCharge implements Serializable {
    private static final long serialVersionUID = 5371282992014101564L;
    private  JedisCluster jedisCluster;
    private AbstractTrackLog trackLog;
    private static final int EXPIRE_TIME = 24*60*60;

    public TrackAVStatisticsCharge(JedisCluster jedisCluster, AbstractTrackLog trackLog){
        this.jedisCluster = jedisCluster;
        this.trackLog = trackLog;
    }


    public void execute(){
        /**过滤重复上报数据*/
        StringBuilder sb = new StringBuilder(Constant.BI_SSTICS_);
        String id = trackLog.getId();
        String timestamp = trackLog.getTimestamp();
        String repeat_key = sb.append("AV_").append(id).append("_").append(timestamp).toString();
        long count =  jedisCluster.incr(repeat_key);
        jedisCluster.expire(repeat_key,60);
        if(count>1){
            System.err.println("重复数据,不做业务处理. "+repeat_key);
            return ;
        }

        DateUtil dateUtil = new DateUtil();
        String day =  dateUtil.timestamp2Day(timestamp);
        String day_hour =  dateUtil.timestamp2Hour(timestamp);
        String hour="99";
        if(day_hour.length()==10){
            hour = day_hour.substring(8,10);
        }
        /**2.统计加载次数**/
        jedisCluster.hincrBy(Constant.BI_SSTICS_AV_ALL_+day,hour,1);
        jedisCluster.expire(Constant.BI_SSTICS_AV_ALL_+day,EXPIRE_TIME);
        /**过滤区分品牌错误数据**/
        String adgres = trackLog.getAdgres();
        if(StringUtils.isBlank(adgres)){
            System.err.println("adgres is null,c_id="+trackLog.getId());
            return ;
        }
        /**3区分品牌中长尾**/
        if("1".equals(trackLog.getAdgres())){
            //品牌点击量
            jedisCluster.hincrBy(Constant.BI_SSTICS_AV_+day,Constant.BRAND_COUNT_+hour,1);
            jedisCluster.expire(Constant.BI_SSTICS_AV_+day,EXPIRE_TIME);

        }
        if("2".equals(trackLog.getAdgres())||"4".equals(trackLog.getAdgres())){
            /**中长尾点击量**/
            jedisCluster.hincrBy(Constant.BI_SSTICS_AV_+day,Constant.SURPLUS_COUNT_+hour,1);
            jedisCluster.expire(Constant.BI_SSTICS_AV_+day,EXPIRE_TIME);

        }

        /**4.计算收入  cpm**/
        if(trackLog.checkIncome(trackLog)&&"4".equals(trackLog.getBidtype())){
            if("1".equals(trackLog.getAdgres())){
                // 品牌
                jedisCluster.hincrBy(Constant.BI_SSTICS_BRAND_CONSUME_+day,Constant.AV_BRAND_+hour,Long.valueOf(trackLog.getCharge()));
                jedisCluster.expire(Constant.BI_SSTICS_BRAND_CONSUME_+day,EXPIRE_TIME);
                //cpc 统计品牌收入计算次数
                jedisCluster.hincrBy(Constant.BI_SSTICS_DCPM_+day,Constant.AV_BRAND_+hour,1);
                jedisCluster.expire(Constant.BI_SSTICS_DCPM_+day,EXPIRE_TIME);
            }

            if("2".equals(trackLog.getAdgres())||"4".equals(trackLog.getAdgres())){
                jedisCluster.hincrBy(Constant.BI_SSTICS_SURPLUS_CONSUME_+day,Constant.AV_SURPLUS_+hour,Long.valueOf(trackLog.getCharge()));
                jedisCluster.expire(Constant.BI_SSTICS_SURPLUS_CONSUME_+day,EXPIRE_TIME);
                //cpc 统计中长尾收入计算次数
                jedisCluster.hincrBy(Constant.BI_SSTICS_DCPM_+day,Constant.AV_SURPLUS_+hour,1);
                jedisCluster.expire(Constant.BI_SSTICS_DCPM_+day,EXPIRE_TIME);
            }

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
