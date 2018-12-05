package com.li.common;

import org.apache.commons.lang.StringUtils;
import redis.clients.jedis.JedisCluster;


/**
 * Created by wangdi on 17/11/2.
 */
public class TrackCheckUtil {
    private static JedisCluster  jedisCluster ;


    public TrackCheckUtil(){}

    public TrackCheckUtil(JedisCluster jedisCluster){
        this.jedisCluster = jedisCluster;
    }

    /**
     * 校验id 是否重复计算过
     * true : 已计算过,false:未计算过
     * @param type_vid
     * @param vid_value
     * @return
     */
    public boolean checkRepeat(String type_vid,String vid_value,String time){
        if(StringUtils.isBlank(type_vid)||StringUtils.isBlank(vid_value)){
            return true;
        }
        String key = type_vid+"_"+vid_value+"_"+time;
        //long count =  jedisCluster.hincrBy(Constant.repeat_key,key,1);
        long count =  jedisCluster.incr(key);
        jedisCluster.expire(key,60);
        if(count>1){
            return true;
        }
        return false;
    }

    /***
     * 业务 数据验证
     * @param type_vid
     * @return false: 验证失败,不做后续计费处理. true: 验证成功
     */
    public boolean checkBusinessData(String type_vid,String vid_value,String time,String status,String isspam,String bidtype){
        /**step.1 log_id, timmestamp 非空验证*/
        if(StringUtils.isBlank(vid_value)||StringUtils.isBlank(time)){
            System.out.println("--time is error--type_vid="+type_vid+",time="+time);
            //collector.ack(tuple);
            return false;
        }

        /**step.3 过滤空广告*/
        if(StringUtils.isNotBlank(status)&&status.trim().equals("0")){
            System.out.println("空广告不计费.type_vid="+vid_value);
            //collector.ack(tuple);
            return false;
        }
        /**step.4 过滤作弊数据*/
        if(StringUtils.isNotBlank(isspam)&&!isspam.trim().equals("0")){
           System.out.println("此id为作弊数据,不做计费处理.type_vid="+vid_value+",isspam="+isspam);
            //collector.ack(tuple);
            return false;
        }
        /**step.5 计费类型非空判断*/
        if(StringUtils.isBlank(bidtype)){
            System.out.println("--bidtype is null,type_vid="+type_vid+",time="+time);
           // collector.ack(tuple);
            return false;
        }
        return true;
    }

    /**
     *
     * @param hkey
     * @param field
     * @param value
     */
    public void setBackLog(String hkey,String field,String value,int expire_time){
        if(StringUtils.isBlank(hkey)||StringUtils.isBlank(field)){
            return ;
        }
        jedisCluster.hset(hkey,field,value);
        jedisCluster.expire(hkey,expire_time);
    }

    /**
     * 记录每小时重复上报 id
     * @param key
     * @param value
     * @param expire_time
     */
    public void setRepeatkey (String key,String value,int expire_time){
        if(StringUtils.isBlank(key)||StringUtils.isBlank(value)){
            return ;
        }
        jedisCluster.lpush(key,value);
        jedisCluster.expire(key,expire_time);
    }



}
