package com.li.core;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * Created by wangdi on 18/9/4.
 * tracknocharge  日志model
 *
 */
public class AbstractTrackNoChargeLog implements Serializable {
    private static final long serialVersionUID = -5493906218345624733L;

    private String id ; //uid
    private String timestamp;  //时间戳
    private String aid;
    private String campid;
    private String adgid;
    private String crid;
    private String crsid; //广告集合
    private String appid;
    private String clickid;
    private String atype;//转化类型
    private String ett;



    public AbstractTrackNoChargeLog build(String tuple,String sp_tuple){
        if(StringUtils.isBlank(tuple)){
            System.out.println("tuple is null");
            return null;
        }

        String[] filed_arr =  tuple.split(sp_tuple);
        if(filed_arr.length==0){
            System.err.println("step1 error message format error ");
            return null;
        }
        AbstractTrackNoChargeLog log = new AbstractTrackNoChargeLog();
        Class classz  = log.getClass();
        /**step2 获取 field 值*/
        for(String filed:filed_arr){
            if(StringUtils.isBlank(filed)){
                System.err.println("step2 error filed is  empty ");
                continue;
            }
            /**上报数据格式 key=value*/
            String[] kv_arr =  filed.split("=");
            if(kv_arr.length==0){
                // System.err.println("step3 filed format is error filed="+filed);
                continue;
            }
            String key = kv_arr[0];
            String value="";//value 初始值是空,可能key=""
            if(kv_arr.length==2){
                value =  kv_arr[1];
            }
            /**step4 bean赋值**/
            try{
                Field field = classz.getDeclaredField(key);
                field.setAccessible(true);
                field.set(log,value.trim());
            }catch (Exception e){
                // e.printStackTrace();
                // System.err.println("step4 java.lang.NoSuchFieldException: container,key= "+key);
                continue;
            }
        }

        return log;


    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getCampid() {
        return campid;
    }

    public void setCampid(String campid) {
        this.campid = campid;
    }

    public String getAdgid() {
        return adgid;
    }

    public void setAdgid(String adgid) {
        this.adgid = adgid;
    }

    public String getCrid() {
        return crid;
    }

    public void setCrid(String crid) {
        this.crid = crid;
    }

    public String getCrsid() {
        return crsid;
    }

    public void setCrsid(String crsid) {
        this.crsid = crsid;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getClickid() {
        return clickid;
    }

    public void setClickid(String clickid) {
        this.clickid = clickid;
    }

    public String getAtype() {
        return atype;
    }

    public void setAtype(String atype) {
        this.atype = atype;
    }

    public String getEtt() {
        return ett;
    }

    public void setEtt(String ett) {
        this.ett = ett;
    }
}
