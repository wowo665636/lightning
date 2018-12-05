package com.li.core;

import com.li.redis.Constant;
import com.li.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.util.MD5Hash;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wangdi on 18/7/2.
 */
public class AbstractTvLog implements Serializable {
    private static final long serialVersionUID = -1133034874009155034L;


    private String time;
    private String suv;
    private String uid;
    private String vp;
    private String plat;
    private String adstyle;
    private String bk;
    private String al;
    private String posid;
    private String spead;
    private String pointtime;




    public AbstractTvLog build(String tuple ,String spilt_flag){
        if(StringUtils.isBlank(tuple)){
            System.out.println("tuple is null");
            return null;
        }
        AbstractTvLog log = new AbstractTvLog();
        Class classz  = log.getClass();
        String[] filed_arr =  tuple.split(spilt_flag);
        /**step1 拆分message*/
        if(filed_arr.length==0){
            System.err.println("step1 error message format error ");
            return null;
        }
        for(String filed :filed_arr){
            if(StringUtils.isBlank(filed)){
                System.err.println("step2 error filed is  empty ");
                continue;
            }
            /**上报数据格式 key=value*/
            String[] kv_arr =  filed.split(Constant.SP_FILED);
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
        Pattern p = Pattern.compile("\\[(.*) \\+0800\\]");
        Matcher m = p.matcher(tuple);
        if (m.find()) {
            String tempStr = m.group(1);
            log.setTime(DateUtil.hyTimeFormat(tempStr)); // 设置 日志时间戳

        }
        if(StringUtils.isNotBlank(log.getTime())){
            log.setPointtime(DateUtil.timestamp2Datetime2(log.getTime()).substring(0,12));
        }

        return  log;


    }

    /**
     * 维度 unquekey
     * @return
     */
    public String uniqueKey(){
        try{
            StringBuilder sb = new StringBuilder()
                    .append("plat").append(Constant.SP_FILED).append(this.getPlat()).append(Constant.DIM_TUPLE)
                    .append("adstyle").append(Constant.SP_FILED).append(this.getAdstyle()).append(Constant.DIM_TUPLE)
                    .append("bk").append(Constant.SP_FILED).append(this.getBk()).append(Constant.DIM_TUPLE)
                    .append("pointtime").append(Constant.SP_FILED).append(this.getPointtime())
                    ;
            DateUtil dateUtil = new DateUtil();
            //String date_time = dateUtil.timestamp2Day(time);
            String date_time_s = dateUtil.timestamp2Datetime2(time);
           // String uid =  MD5Hash.getMD5AsHex(sb.toString().getBytes())+"_"+date_time_s.substring(0,12);
            return sb.toString();
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取 unique key hash id
     * @return
     */
    public String getMD5Hash(){
        String date_time = DateUtil.timestamp2Day(time);//天
        return  MD5Hash.getMD5AsHex(this.uniqueKey().getBytes())+"_"+date_time;
    }



    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSuv() {
        return suv;
    }

    public void setSuv(String suv) {
        this.suv = suv;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getVp() {
        return vp;
    }

    public void setVp(String vp) {
        this.vp = vp;
    }

    public String getPlat() {
        return plat;
    }

    public void setPlat(String plat) {
        this.plat = plat;
    }

    public String getAdstyle() {
        return adstyle;
    }

    public void setAdstyle(String adstyle) {
        this.adstyle = adstyle;
    }

    public String getBk() {
        return bk;
    }

    public void setBk(String bk) {
        this.bk = bk;
    }

    public String getAl() {
        return al;
    }

    public void setAl(String al) {
        this.al = al;
    }

    public String getPosid() {
        return posid;
    }

    public void setPosid(String posid) {
        this.posid = posid;
    }

    public static void main(String[] args) {
        String mm = "v2\t[02/Jul/2018:15:00:02 +0800]\t120.38.213.229\t\"1805041514047769\"\tc=1&v1=1048&v2=1128&p=oad1,oad2&loc=CN3507&adstyle=oad&ac=24306&ad=574709&pt=29118&b=639359&bk=149954939&at=30&spead=2&du=2408&adtime=75&trule=38109&mx=5&al=9344706&out=0&au=1&vid=4300405&tvid=94676031&rt=83ef0e14178462c84a68ce3fe62da104&spead=2&uv=15292235641445424916&uuid=23419a19-32eb-b8b1-ea48-59c8fa99b33a&UUID=23419a19-32eb-b8b1-ea48-59c8fa99b33a&vt=vrs&rd=tv.sohu.com&fee=0&isIf=0&suv=1805041514047769&uid=15292235641445424916&sz=1309_605&md=JhwptAuz33nY9Cf9CqfGrKixM/AHYBiraPo2CA==200&crid=730&scookie=1&ar=6&sign=XdmRv5suOcLnpVf7eoJwfMmkMZevjP_H9XGbmSaXi5uKzPwo0z3KX9lC9h-4_1_otYEQGfq_pI8.&rip=120.38.213.229&sip=10.11.161.89&fip=120.38.213.229&url=https%3A//tv.sohu.com/20171114/n600253877.shtml%3Ftxid%3D4e4df35dda9d8ed32c874b1ad590ef59&pagerefer=https%253A//so.360kan.com/index.php%253Fkw%253D%2525E6%252588%252591%2525E7%25259A%252584%2525E4%2525BD%252593%2525E8%252582%2525B2%2525E8%252580%252581%2525E5%2525B8%252588%2526from%253D&ti=5oiR55qE77yB5L2T6IKy6ICB5biI56ysNOmbhg==&lrd=https%253A%252F%252Fso.360kan.com%252Findex.php%253Fkw%253D%2525E6%252588%252591%2525E7%25259A%252584%2525E4%2525BD%252593%2525E8%252582%2525B2%2525E8%252580%252581%2525E5%2525B8%252588%2526from%253D&plat=pc&adplat=0&v1=1048&v2=1128&offline=0&endtime=20180731&ad=574709&b=639359&bk=149954939&pagetype=1&dspid=10175&suid=1805041514047769&seq=1&w=960&h=388&cheattype=0&bidid=7ebc3510781f47f5bb7d7728b8aa02ff&sperotime=1530514770&tuv=15292235641445424916&template=normal,null&platsource=tvpc&vp=e&encrypt=YxzpN5QQbDR2_W2hNymamih6p-tU2-bE7HvQgaYua-iK25kT\t\"https://tv.sohu.com/upload/swf/20180509/Main.swf\"\t\"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36\"\t10.10.74.203\t-\t200";
        String [] params = mm.split("[&?\\s]");
        for(String cc :params){
            System.out.println("--:  "+cc);
        }

        System.out.println("20180704115523".substring(0,12));


    }

    public String getSpead() {
        return spead;
    }

    public void setSpead(String spead) {
        this.spead = spead;
    }

    public String getPointtime() {
        return pointtime;
    }

    public void setPointtime(String pointtime) {
        this.pointtime = pointtime;
    }
}
