package com.li.core;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * Created by wangdi on 17/12/26.
 */
public class AbstractSearchLog implements Serializable {

    private static final int EXPIRE_TIME = 24*60*60;
    private static final long serialVersionUID = -2768970320363477683L;

    public final String sp_filed="=";
    private String id ; //uid
    private String timestamp;  //时间戳
    private String crid;
    private String appid;//区分平台媒体
    private String adslotid; // 广告位置id
    private String developerid;// 开发者id
    private String unionappid;// 应用id
    private String currentTime; //search  时间戳




    public AbstractSearchLog(){

    }

    /**
     * 根据 kafka message 构建 tracking log model
     * @param tuple
     * @return
     */
    public AbstractSearchLog build (String tuple, String spilt_flag){
        if(StringUtils.isBlank(tuple)){
            System.out.println("tuple is null");
            return null;
        }
        String current_time = tuple.substring(0,19).replaceAll("[-:\\s]", "");//获取search日志时间戳
        tuple  = tuple.substring(20,tuple.length());
        AbstractSearchLog log =  new AbstractSearchLog();
        log.setCurrentTime(current_time);
        Class classz = log.getClass();
        /**step1 拆分message*/
        String[] filed_arr  = tuple.split(spilt_flag);
        if(filed_arr.length==0){
            System.err.println("step1 error message format error ");
            return null;
        }
        /**step2 获取 field 值*/
        for(String filed:filed_arr){
            if(StringUtils.isBlank(filed)){
                System.err.println("step2 error filed is  empty ");
                continue;
            }
            /**上报数据格式 key=value*/
            String[] kv_arr =  filed.split(sp_filed);
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







    /**
     * 验证上报数据是否正确
     * @param log
     * @return
     */
    public boolean checkBusinessData(AbstractSearchLog log){
        if(log==null){
            System.err.println("message model is null");
            return false;
        }
        if(StringUtils.isBlank(log.getId())||StringUtils.isBlank(log.getCurrentTime())){
            System.err.println("message id/time is error,id="+log.getId()+"time="+log.getCurrentTime());
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        String tt = "2018-04-02 00:00:00 p=1522598400 id=0329e844f4a8421b6_0_0 hostname= isdebug= sspid=1 platform_id=2 preferres= appid=pcnews newsid= subid= newschn=1000000000 vid= contentres= vc= ugccode2= vplat= developerid= unionappid= os= osv= model= scs= nets=0 ssid= bssid= manufacturer= carrierid=0 appv= pid= newsdkpid= poid= newswappid= tvpid= h5tvpid= pageurl= mac= idfa= idfv= adsid= imei= imsi= androidid= cid= suv= yyid= tuv= lsc= ssc= ip=222.128.117.159 geoid1=1156110000 geoid2=1156110000 adslotid=15538 turn=3 adslottype=205 w=300 h=250 blacktempls= reposition= lc= rr= tt1=1522598403110 tt2=1522598400313 aid=10002859 campid=20030829 adgid=40115765 crid=100267744 priority=250 bidtype=1 bidstype=1 bid=10000000 charge=10000 nongspcharge=10000 adgres=1 template=normal_iframe_300*250 template_id=86 industryid=0 accounttype=0 rankscore=10000000.0000 pclick=0.0000 ecpm=0.0000 bucket= ext= isgsp=0 crsid=0 noad= isspam= md5= bctimetype= freqinfo= atx=31 rt_all=12 rt_req=0 rt_reqmod=0 rt_fc=0 rt_fc_kv=0 rt_pclick=0 rt_auc=0 rt_auc_kv1= rt_auc_kv2= rt_render=1 rt_dmp_ct_kv=0 rt_at_kv1=1 rt_at_kv2=0 rt_auc_adx=0 rt_bs=6 rt_mc=1 rt_adx=2859584756 rt_api=0 targettype=1 targetsetting= feaid= feaweight= audienceid= expid=30000 dspsource= dspid= bidid= delivertype=0 advid= rtblevel= markettag= sspid_adx= ad_title= pctr_status=random bs_status=success mc_status=success adx_status= api_status= n_bs=5 n_offrun=0 n_fc=0 n_auc=5 n_dedup=0 n_adx_req=0 n_adx_resp=0 n_api_req=0 n_api_resp=0 n_pos=0 n_primer=";
        String str = tt.substring(0,19);

        System.out.println(str.replaceAll("[-:\\s]", "").substring(0,8));
        System.out.println(tt.substring(0,19));
        System.out.println(tt.substring(20,tt.length()));
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

    public String getCrid() {
        return crid;
    }

    public void setCrid(String crid) {
        this.crid = crid;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getAdslotid() {
        return adslotid;
    }

    public void setAdslotid(String adslotid) {
        this.adslotid = adslotid;
    }

    public String getDeveloperid() {
        return developerid;
    }

    public void setDeveloperid(String developerid) {
        this.developerid = developerid;
    }

    public String getUnionappid() {
        return unionappid;
    }

    public void setUnionappid(String unionappid) {
        this.unionappid = unionappid;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }
}
