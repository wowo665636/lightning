package com.li.redis;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangdi on 17/6/23.
 */
public class Constant {
    public static final String BI  = "bi_";

    public static final String IMP_AV = "ads_av_";//曝光量
    public static final String LOAD_CONS = "ads_load_";//加载消耗量
    public static final String CLICK = "ads_click_";//点击量

    public static final Integer IMP_AV_TYPE = 1;//曝光量
    public static final Integer LOAD_CONS_TYPE = 2;//加载消耗量
    public static final Integer CLICK_TYPE = 3;//点击量
    public static final Integer CONSUME_TYPE = 4;//消耗量
    public static final String IMP_AV_TYPE_STR = "1";//曝光量
    public static final String LOAD_CONS_TYPE_STR   = "2";//加载消耗量
    public static final String CLICK_TYPE_STR = "3";//点击量
    public static final String CONSUME_TYPE_STR = "4";//点击量
    //c_id|v_id|av_id
    public static final String c_id = "c_id";
    public static final String v_id = "v_id";
    public static final String av_id = "av_id";
    public static final String  repeat_key = "repeat_key";// 数据是否重复key
    // redis 消耗金额key
    public static final String consume_amount="consume_amount_";
    // redis 展示量key
    public static final String imp_amount="imp_amount_";

    public static final String REDIS_POOL_22149="REDIS_POOL_22149";

    public static final String REDIS_CLUSTER_SEG=",";

    public static final String REDIS_CLUSTER_HOST_SEG=":";

    public static final String TRACKING_ID = "id";

    public static final String TC_REPEAT_V_KEY = "tc_repeat_v_key_";
    public static final String TC_REPEAT_AV_KEY = "tc_repeat_av_key_";
    public static final String TC_REPEAT_C_KEY = "tc_repeat_c_key_";

    /****storm config start***/
    public static final String NIMBUS_HOST_REMOTE = "maven.nimbus.host.remote";
    public static final String ZK_HOST_REMOTE = "maven.zk.host.remote";
    public static final String SPOUT_ZK_HOST = "maven.spout.zk.host";
    public static final String KAFKA_BROKER_ZK_PATH ="maven.kafka.broker.zk.path";
    public static final String  KAFKA_TOPIC = "maven.kafka.topic";

    public static final String NIMBUS_SEEDS = "maven.nimbus.seeds";
    public static final String KAFKA_BROKER = "maven.kafka.broker";

    /****storm config end***/

    /******统计指标  start*******/

    public static final String BI_SSTICS_ = "bi_sstics_";// 统计指标redis key 前缀

    public static final String BI_SSTICS_C_ALL_ = "bi_sstics_c_all_";// 点击上报次数
    public static final String BI_SSTICS_C_ = "bi_sstics_c_"; //点击key
    public static final String C_BRAND_ = "c_brand_";//c 品牌收入
    public static final String C_SURPLUS_ = "c_surplus_";//c 中长尾收入
    public static final String BI_SSTICS_CPC_ = "bi_sstics_cpc_" ;//cpc 统计量

    public static final String BI_SSTICS_V_ALL_ = "bi_sstics_v_all_";//加载上报次数
    public static final String BI_SSTICS_V_EMPTY_ = "bi_sstics_v_empty_";//空广告
    public static final String BI_SSTICS_V_ = "bi_sstics_v_"; //加载
    public static final String V_BRAND_ = "v_brand_";//v 品牌 收入
    public static final String V_SURPLUS_ = "v_surplus_";//v 中长尾收入
    public static final String BI_SSTICS_CPM_ = "bi_sstics_cpm_" ;//cpm 统计量

    public static final String BI_SSTICS_AV_ALL_ = "bi_sstics_av_all_";// av 上报总数
    public static final String BI_SSTICS_AV_ = "bi_sstics_av_"; //展示
    public static final String AV_BRAND_ = "av_brand_";//av 品牌 收入
    public static final String AV_SURPLUS_ = "av_surplus_";//av 中长尾收入
    public static final String BI_SSTICS_DCPM_ = "bi_sstics_dcpm_" ;//dcpm 统计量


    public static final String BRAND_COUNT_ = "brand_count_"; // 品牌
    public static final String SURPLUS_COUNT_ = "surplus_count_"; // 中长尾
    public static final String BI_SSTICS_BRAND_CONSUME_ = "bi_sstics_brand_consume_";//统计收入 品牌
    public static final String BI_SSTICS_SURPLUS_CONSUME_ = "bi_sstics_surplus_consume_";//统计收入 中长尾


    /********统计指标  end*****/
    public static final String aid = "aid=";
    public static final String campid = "campid=";
    public static final String adgid = "adgid=";
    public static final String crid = "crid=";

    /******redis sql rule********/

    public static final String SQL_RULE_TREND = "sql_rule_trend";
    public static final String TABLE = "TABLE";
    public static final String FIELDS = "FIELDS";
    public static final String WHERE = "WHERE";
    public static final String GROUP = "GROUP";
    public static final String SELECT = "SELECT";

    public static final String BI_BRAND_ = "bi_brand_";
    public static final String BI_EFFECT_ = "bi_effect_";

    public static final String BI_TREND_ = "bi_trend_";


    public static final String BI_TREND_V_ = "bi_trend_v_";
    public static final String BI_TREND_AV_ = "bi_trend_av_";
    public static final String BI_TREND_C_ = "bi_trend_c_";
    public static final String BI_TREND_NA_ = "bi_trend_na_";
    public static final String BI_TREND_NAA_ = "bi_trend_naa_";
    public static final String BI_TREND_NA_PLAT = "bi_trend_na_plat_";
    public static final String BI_TREND_NAA_PLAT= "bi_trend_naa_plat_";

    public static final String BI_TREND_CONSUME_ = "bi_trend_consume_";

    public static final String BI_SQL_UNEQUAL = "!=";
    public static final String BI_SQL_NOT_IN = "NOT IN";

    /****Customization 定制topo*****/

    public static final String BI_CUSTOM_ = "bi_custom_";
    public static final String BI_CUSTOM_RULE="bi_custom_rule";
    public static final String BI_DIM_RULE="bi_dim_rule";
    public static final String BI_DIM_ADSERVICE_RULE="bi_dim_adservice_rule";
    public static final String BI_ADSERVICE_="bi_adservice_";
    public static final String NA_ = "na_";
    public static final String NAA_ = "naa_";
    public static final String V_ = "v_";
    public static final String AV_ = "av_";
    public static final String C_ = "c_";
    public static final String CONSUME_ = "consume_";
    public static final String UNION_CROSS_TABLE = "union_cross_table";//混投数据推送key
    public static final String TRANS_CLICK = "trans_click";//点击转化数据推送

    public static final String COUNT_UV="bi_custom_count_uv";// uv统计 redis 配置规则key
    public static final String IDFA_ = "idfa_";// uv hash key
    public static final String IMEI_ = "imei_";// imei_ hash key
    public static final String CID_ = "cid_";// uv hash key
    public static final String TIME_SUFFIX=" 23:59:59";

    public static final String BI_SEARCH_ = "bi_search_";
    public static final String SQL_RULE_SEARCH = "sql_rule_search";
    public static final String SP_BLANK="\\s";
    public static final String SP_TUPLE="\t";
    public static final String SP_FILED="=";
    public static final String DIM_TUPLE="&";


    public static final String BI_CUSTOM_NOCHARGE_RULE="bi_custom_nocharge_rule";
    /*********warehouse topo************************************/

    public static final String BI_WAREHOUSE_ = "bi_warehouse_";
    public static final String NUM_ = "num_";
    public static final String T_XPS2_TRACK_STORM_ADSLOTID = "t_xps2_track_storm_adslotid";
    //----------HY
    /**
     * 计算模式：直接累加
     */
    public static String CalMode_Add = "add";
    /**
     * 计算模式：spilt , 后，计算原key length次
     */
    public static String CalMode_Split_Add = "split_add";
    /**
     * 计算模式：spilt , 后，key_元素   分别计算
     */
    public static String CalMode_Split_Add_key = "split_add_key";
    /**
     * 分隔符
     */
    public static String Separator = ",";
    /**
     * 解析方式和topic对应方式： 解析json格式； 解析url格式
     */
    public static String Parse_Json = "parseJson";
    public static String Parse_Url = "parseUrl";

    /**
     * Kafka Topic, 新品算请求日志; 新品算上报日志
     */
    public static final String TOPIC_MILESTONE_REQ = "Milestone-req";
    public static final String TOPIC_MILESTONE_RES = "Milestone-res";

    public static Map<String, String> parseMap = new HashMap<String, String>();

    static {
        parseMap.put("adserver_request_log", Parse_Url);    // 新闻请求日志：各端

        parseMap.put("countinfo", Parse_Json);          // 新闻上报：pc
        parseMap.put("countinfoh5", Parse_Json);        // 新闻上报：wap
        parseMap.put("countinfomobile", Parse_Json);    // 新闻上报：app
        parseMap.put("mobilealliance", Parse_Json);     // 新闻上报：移动联盟

        parseMap.put("tvadfront-pc", Parse_Url);        // 视频请求日志：pc
        parseMap.put("tvadfront-mobile", Parse_Url);    // 视频请求日志：移动
        parseMap.put("tvadfront-page", Parse_Url);      // 视频请求日志：Page页
        parseMap.put("tvadfront-ifox", Parse_Url);      // 视频请求日志：ifox （搜狐影音）

        parseMap.put("newtvadpv", Parse_Url);      // 视频上报：pc
        parseMap.put("tvadmpv", Parse_Url);        // 视频上报：移动
        parseMap.put("tvadppv", Parse_Url);        // 视频上报：Page页
        parseMap.put("tvadfpv", Parse_Url);        // 视频上报：搜狐影音

        parseMap.put("player_err_pingback", Parse_Url);        // 播放器错误码上报

        parseMap.put(TOPIC_MILESTONE_REQ, Parse_Url);      // 新品算，请求日志
        parseMap.put(TOPIC_MILESTONE_RES, Parse_Url);      // 新品算，上报日志
    }


    /**视频redis key*/
    public static final String T_BI_TV_DETAIL = "t_bi_real_time_tv";






}
