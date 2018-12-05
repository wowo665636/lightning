package com.li.core;

import com.li.redis.Constant;
import com.li.util.DateUtil;
import org.apache.hadoop.hbase.util.MD5Hash;

import java.io.Serializable;

/**
 * Created by wangdi on 18/7/23.
 */
public class TdwdStormAid implements Serializable {
    private static final long serialVersionUID = -4151129658424131177L;
    //---------维度
    private String appid;  //~
    private String adslotid;//广告位id ~
    private String accounttype;// 0:ae,1:汇算~
    private String appdelaytrack;//APPdelaytrack=0（正常上报），=1（缓存上报）的情况~
    private String os;//操作系统
    private String os_rename; //操作系统名称截取~
    private String rr;//推荐流刷新次数
    private String rr_20;//推荐流刷新次数~
    private String bidtype;// 计费类型 ~
    private String is_effect; //售卖条件
    private String priority;
    private String priority_f;// (10,75);(70以下);(100以上);(other) ~
    private String timestamp;  //时间戳
    private String ac ;// 资讯版 av重复上报数
    private String ac_20;//~
    private String ett;
    private String charge; //计费信息
    private String dwd_uid;
    private String md5AsHex;
    private String aid;
    private String campid;
    private String adgid;
    private String crid;


    //---------指标
    private long count_na;
    private long count_naa;
    private long count_v;  // 非竞价:优先级>75 ,自助竞价:70或75 and accounttype=1
    private long count_av; // 同上
    private long count_click;// 同上
    private long sum_charge; //收入 同上
    private long count_num;
    // 非竞价 库存, 总库存
    /**
     * 拼装维度unionkey 10 个
     * @return
     */
    public String getUniquenKey(){
        StringBuilder sb = new StringBuilder()
                .append("appid").append(Constant.SP_FILED).append(this.getAppid()).append(Constant.DIM_TUPLE)
                .append("adslotid").append(Constant.SP_FILED).append(this.getAdslotid()).append(Constant.DIM_TUPLE)
                .append("accounttype").append(Constant.SP_FILED).append(this.getAccounttype()).append(Constant.DIM_TUPLE)
                .append("os_rename").append(Constant.SP_FILED).append(this.getOs_rename()).append(Constant.DIM_TUPLE)
                .append("ac_20").append(Constant.SP_FILED).append(this.getAc_20()).append(Constant.DIM_TUPLE)
                .append("bidtype").append(Constant.SP_FILED).append(this.getBidtype()).append(Constant.DIM_TUPLE)
                .append("is_effect").append(Constant.SP_FILED).append(this.getIs_effect()).append(Constant.DIM_TUPLE)
                .append("priority_f").append(Constant.SP_FILED).append(this.getPriority_f()).append(Constant.DIM_TUPLE)
                .append("ett").append(Constant.SP_FILED).append(this.getEtt())
                .append("aid").append(Constant.SP_FILED).append(this.getAid())
                ;
        //.append("templateid").append(Constant.SP_FILED).append(this.getTemplateid());
        DateUtil dateUtil = new DateUtil();
        //String m_time = dateUtil.timestamp2DateMinute(timestamp);
        String time = dateUtil.timestamp2Hour(timestamp);
        // String time = String.valueOf(Long.MAX_VALUE-Long.valueOf(timestamp));
        this.md5AsHex =  MD5Hash.getMD5AsHex(sb.toString().getBytes())+"_"+time;
        this.dwd_uid = sb.toString();
        return dwd_uid;
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getRr() {
        return rr;
    }

    public void setRr(String rr) {
        this.rr = rr;
    }

    public String getRr_20() {
        return rr_20;
    }

    public void setRr_20(String rr_20) {
        this.rr_20 = rr_20;
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

    public String getBidtype() {
        return bidtype;
    }

    public void setBidtype(String bidtype) {
        this.bidtype = bidtype;
    }

    public String getIs_effect() {
        return is_effect;
    }

    public void setIs_effect(String is_effect) {
        this.is_effect = is_effect;
    }

    public long getCount_na() {
        return count_na;
    }

    public void setCount_na(long count_na) {
        this.count_na = count_na;
    }

    public long getCount_naa() {
        return count_naa;
    }

    public void setCount_naa(long count_naa) {
        this.count_naa = count_naa;
    }

    public long getCount_v() {
        return count_v;
    }

    public void setCount_v(long count_v) {
        this.count_v = count_v;
    }

    public long getCount_av() {
        return count_av;
    }

    public void setCount_av(long count_av) {
        this.count_av = count_av;
    }

    public long getCount_click() {
        return count_click;
    }

    public void setCount_click(long count_click) {
        this.count_click = count_click;
    }

    public long getSum_charge() {
        return sum_charge;
    }

    public void setSum_charge(long sum_charge) {
        this.sum_charge = sum_charge;
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

    public String getAc() {
        return ac;
    }

    public void setAc(String ac) {
        this.ac = ac;
    }

    public String getAc_20() {
        return ac_20;
    }

    public void setAc_20(String ac_20) {
        this.ac_20 = ac_20;
    }

    public String getDwd_uid() {
        return dwd_uid;
    }

    public void setDwd_uid(String dwd_uid) {
        this.dwd_uid = dwd_uid;
    }

    public long getCount_num() {
        return count_num;
    }

    public void setCount_num(long count_num) {
        this.count_num = count_num;
    }

    public String getEtt() {
        return ett;
    }

    public void setEtt(String ett) {
        this.ett = ett;
    }

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    public String getMd5AsHex() {
        return md5AsHex;
    }

    public void setMd5AsHex(String md5AsHex) {
        this.md5AsHex = md5AsHex;
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
}
