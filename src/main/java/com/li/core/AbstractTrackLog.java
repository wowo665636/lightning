package com.li.core;

import com.li.util.EtlUtils;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * Created by wangdi on 17/12/26.
 */
public class AbstractTrackLog implements Serializable {

    private static final long serialVersionUID = -4447192898035137135L;
    private static final int EXPIRE_TIME = 24*60*60;

    public  final String sp_tuple="\t";
    public final String sp_filed="=";
    public final String dim_tuple="&";
    private String id ; //uid
    private String timestamp;  //时间戳
    private String aid;
    private String campid;
    private String adgid;
    private String crid;
    private String bidtype;//计费类型
    private String charge; //计费信息
    private String isspam;//正常计费:0,超投不计费:111
    private String adgres;// 1. 品牌,2:中长尾cpm,3:中长尾cpc ,8:其他
    private String ett;//na:空广告,v:加载 ,av:展示,click:点击 ,tel:电话,视频相关:vp,关闭:close,超时:to,错误:e
    private String sspid;//1:搜狐门户,2:搜狐视频
    private String platformid;//1.APP,2:PC,3:WAP
    private String status;
    private String appid;//区分平台媒体
    private String accounttype;// 0:ae,1:汇算
    private String appdelaytrack;//APPdelaytrack=0（正常上报），=1（缓存上报）的情况
    private String adslotid; // 广告位置id
    private String reposition;// 位置
    private String newschn;// 频道
    private String lc;//上滑刷新次数',
    private String rr;//推荐流刷新次数
    private String rr_20;
    private String appv;//客户端版本号
    private String sdkv;//SDK版本号
    private String unionappid;//联盟 应用id
    private String developerid;//联盟开发者 id
    private String imei;
    private String imsi;
    private String idfa;
    private String idfv;
    private String adsid;
    private String cid;
    private String tuv;
    private String yyid;
    private String suv;
    private String os;//操作系统
    private String os_rename; //操作系统名称截取
    private String templateid;// 模板id
    private String template;// 模板
    private String vplat; //终端
    private String tvpid; // tv 发版渠道
    private String position;//视频位置
    private String vc;// 视频频道
    private String ac ;// 资讯版 av重复上报数
    private String ac_20;
    private String is_effect; //售卖条件
    private String priority;
    private String priority_f;// (10,75);(70以下);(100以上);(other)
    private String abtestid;
    private String pid;

    private String crsid; //创意集合
    private String ptsspid;//媒体
    private String ptplatformid;//平台
    private String ptadtype;//广告类型







    public AbstractTrackLog(){
    }

    /**
     * 根据 kafka message 构建 tracking log model
     * @param tuple
     * @return
     */
    public AbstractTrackLog build(String tuple,String spilt_flag){
        AbstractTrackLog log = new AbstractTrackLog();
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


        if(StringUtils.isNotBlank(log.getOs())){
            if(log.getOs().toLowerCase().indexOf("ios")!=-1){
                log.setOs_rename("ios");

            }else if(log.getOs().toLowerCase().indexOf("android")!=-1){
                log.setOs_rename("android");
            }

        }else{
            log.setOs_rename("unknown");
        }

        log.setRr_20(log.getRr());
        if(StringUtils.isNotBlank(log.getRr())&&EtlUtils.isNumeric(log.getRr()) ){
            int rr = Integer.valueOf(log.getRr()).intValue();
            if(rr>1){
                log.setRr_20("9999");
            }

        }
        log.setAc_20(log.getAc());
        if(StringUtils.isBlank(log.getAc())){
            log.setAc_20("1");
        }else{
            if(EtlUtils.isNumeric(log.getAc())){
                int ac = Integer.valueOf(log.getAc()).intValue();
                if(ac>1){
                    log.setAc_20("9999");
                }
            }
        }

        if(StringUtils.isNotBlank(log.getPriority())&& EtlUtils.isNumeric(log.getPriority())){
            int priority_f = Integer.valueOf(log.getPriority()).intValue();

            if(priority_f==70||priority_f==75){

                log.setPriority_f("p_70_75");

            } else if(priority_f<70){

                log.setPriority_f("p_lt_70");

            } else if(priority_f>=100){

                log.setPriority_f("p_ge_100");
            }else {
                log.setPriority_f("other");
            }

        }


        return  log;
    }



    /**
     * 是否作弊数据
     * @param log
     * @return
     */
    public boolean checkIsSpam(AbstractTrackLog log){
        if(log==null){
            System.err.println("message model is null");
            return false;
        }
        if(StringUtils.isNotBlank(log.getIsspam())&&!log.getIsspam().trim().equals("0")){
           // System.out.println("此id为作弊数据,不做计费处理. id="+log.getId()+",isspam="+log.getIsspam());
            return true;
        }
        return false;

    }



    /**
     * 验证上报数据是否正确
     * @param log
     * @return
     */
    public boolean checkBusinessData(AbstractTrackLog log){
        if(log==null){
            System.err.println("message model is null");
            return false;
        }
        if(StringUtils.isBlank(log.getId())||StringUtils.isBlank(log.getTimestamp())||StringUtils.isBlank(log.getEtt())){
            //System.err.println("--id,time,ett is error,id="+log.getId()+",time="+log.getTimestamp()+",ett="+log.getEtt());
            return false;
        }
        return true;
    }
    /**
     * 是否能计费
     * true:可以计费, false: 不能计费
     * @param log
     * @return
     */
    public boolean checkIncome(AbstractTrackLog log){
        if(log==null){
            System.err.println("message model is null");
            return false;
        }

        if(StringUtils.isBlank(log.getBidtype())||StringUtils.isBlank(log.getCharge())){
            //System.out.println("--bidtype,charge is null,type_vid="+log.getId()+" ,bid_type="+log.getBidtype()+",charge="+log.getCharge());
            return false;
        }

        return true;
    }

    /**
     * 资讯版 只有loading =>ac='',和 ac=1 计费
     * @param log
     * @return
     */
    public boolean checkAcIncome(AbstractTrackLog log){
        if(log==null){
            System.err.println("checkSquirrelIncome error,message model is null");
            return false;
        }
        if(StringUtils.isNotBlank(log.getAc())&&!"1".equals(log.getAc())){
            System.out.println("不计费,appid="+log.getAppid()+",ac="+log.getAc());
            return false;
        }
        return true;
    }

    /**
     * 获取uid
     * @return
     */
    public String getUid(){
        if(StringUtils.isBlank(appid)){
            System.err.println("appid is null,cannot get uid");
            return null;
        }
        switch (appid.trim()){
            //imei/imsi/idfa/adsid/cid/tuv
            case "news":
                return getAppUid(os);

            case "newssdk":

                return getAppUid(os);

            case "wapnews":

                return suv;

            case "pcnews":
                return getPcUid();

            case "tv":
                return getAppUid(os);

            case "h5tv":
                return suv;

            case "pctv":
                return getPcUid();

            case "union":
                return getAppUid(os);

            case "wapunion":
                return suv;
            default:
                break;

        }

        return null;
    }

    /**
     * 获取pc uid
     * @return
     */
    public String getPcUid(){
        String[] pc_arr= {yyid,suv};
        for(String uid :pc_arr){
            if(StringUtils.isNotBlank(uid)){
                return uid;
            }
        }
        return null;
    }



    /**
     * app 获取uid
     * @param os
     * @return
     */
    public String getAppUid(String os){
        //imei/imsi/idfa/adsid/cid/tuv
        String[] ios_arr= {idfa,idfv,adsid,cid,tuv};
        String[] android={imei,imsi,adsid,cid,tuv};

        if(os.toLowerCase().indexOf("ios")!=-1){
            for(String uid :ios_arr){
                if(StringUtils.isNotBlank(uid)){
                    return uid;
                }
            }
            return null;
        }else if(os.toLowerCase().indexOf("android")!=-1){
            for(String uid :android){
                if(StringUtils.isNotBlank(uid)){
                    return uid;
                }
            }
            return null;
        }else{
            if(StringUtils.isNotBlank(cid)){
                return cid;
            }
            if(StringUtils.isNotBlank(tuv)){
                return  tuv;
            }
            return null;
        }


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

    public String getBidtype() {
        return bidtype;
    }

    public void setBidtype(String bidtype) {
        this.bidtype = bidtype;
    }

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }



    public String getIsspam() {
        return isspam;
    }

    public void setIsspam(String isspam) {
        this.isspam = isspam;
    }

    public String getAdgres() {
        return adgres;
    }

    public void setAdgres(String adgres) {
        this.adgres = adgres;
    }

    public String getEtt() {
        return ett;
    }

    public void setEtt(String ett) {
        this.ett = ett;
    }

    public String getSspid() {
        return sspid;
    }

    public void setSspid(String sspid) {
        this.sspid = sspid;
    }

    public String getPlatformid() {
        return platformid;
    }

    public void setPlatformid(String platformid) {
        this.platformid = platformid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getAccounttype() {
        return accounttype;
    }

    public void setAccounttype(String accounttype) {
        this.accounttype = accounttype;
    }

    public String getAppdelaytrack() {
        return appdelaytrack;
    }

    public void setAppdelaytrack(String appdelaytrack) {
        this.appdelaytrack = appdelaytrack;
    }

    public String getAdslotid() {
        return adslotid;
    }

    public void setAdslotid(String adslotid) {
        this.adslotid = adslotid;
    }

    public String getReposition() {
        return reposition;
    }

    public void setReposition(String reposition) {
        this.reposition = reposition;
    }

    public String getNewschn() {
        return newschn;
    }

    public void setNewschn(String newschn) {
        this.newschn = newschn;
    }

    public String getLc() {
        return lc;
    }

    public void setLc(String lc) {
        this.lc = lc;
    }

    public String getRr() {
        return rr;
    }

    public void setRr(String rr) {
        this.rr = rr;
    }

    public String getAppv() {
        return appv;
    }

    public void setAppv(String appv) {
        this.appv = appv;
    }

    public String getSdkv() {
        return sdkv;
    }

    public void setSdkv(String sdkv) {
        this.sdkv = sdkv;
    }

    public String getUnionappid() {
        return unionappid;
    }

    public void setUnionappid(String unionappid) {
        this.unionappid = unionappid;
    }

    public String getDeveloperid() {
        return developerid;
    }

    public void setDeveloperid(String developerid) {
        this.developerid = developerid;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public String getIdfa() {
        return idfa;
    }

    public void setIdfa(String idfa) {
        this.idfa = idfa;
    }

    public String getIdfv() {
        return idfv;
    }

    public void setIdfv(String idfv) {
        this.idfv = idfv;
    }

    public String getAdsid() {
        return adsid;
    }

    public void setAdsid(String adsid) {
        this.adsid = adsid;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getTuv() {
        return tuv;
    }

    public void setTuv(String tuv) {
        this.tuv = tuv;
    }

    public String getYyid() {
        return yyid;
    }

    public void setYyid(String yyid) {
        this.yyid = yyid;
    }

    public String getSuv() {
        return suv;
    }

    public void setSuv(String suv) {
        this.suv = suv;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;


    }


    public String getOs_rename() {
        return os_rename;
    }

    public void setOs_rename(String os_rename) {
        this.os_rename = os_rename;
    }

    public String getTemplateid() {
        return templateid;
    }

    public void setTemplateid(String templateid) {
        this.templateid = templateid;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getVplat() {
        return vplat;
    }

    public void setVplat(String vplat) {
        this.vplat = vplat;
    }

    public String getTvpid() {
        return tvpid;
    }

    public void setTvpid(String tvpid) {
        this.tvpid = tvpid;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getVc() {
        return vc;
    }

    public void setVc(String vc) {
        this.vc = vc;
    }

    public String getAc() {
        return ac;
    }

    public void setAc(String ac) {
        this.ac = ac;
    }

    public String getRr_20() {
        return rr_20;
    }

    public void setRr_20(String rr_20) {
        this.rr_20 = rr_20;
    }

    public String getAc_20() {
        return ac_20;
    }

    public void setAc_20(String ac_20) {
        this.ac_20 = ac_20;
    }

    public String getIs_effect() {
        return is_effect;
    }

    public void setIs_effect(String is_effect) {
        this.is_effect = is_effect;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getPriority_f() {
        return priority_f;
    }

    public void setPriority_f(String priority_f) {
        this.priority_f = priority_f;
    }

    public String getAbtestid() {
        return abtestid;
    }

    public void setAbtestid(String abtestid) {
        this.abtestid = abtestid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getCrsid() {
        return crsid;
    }

    public void setCrsid(String crsid) {
        this.crsid = crsid;
    }

    public String getPtsspid() {
        return ptsspid;
    }

    public void setPtsspid(String ptsspid) {
        this.ptsspid = ptsspid;
    }

    public String getPtplatformid() {
        return ptplatformid;
    }

    public void setPtplatformid(String ptplatformid) {
        this.ptplatformid = ptplatformid;
    }

    public String getPtadtype() {
        return ptadtype;
    }

    public void setPtadtype(String ptadtype) {
        this.ptadtype = ptadtype;
    }
}
