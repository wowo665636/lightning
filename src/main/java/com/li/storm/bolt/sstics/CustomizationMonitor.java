package com.li.storm.bolt.sstics;


import com.li.core.AbstractTrackLog;
import com.li.core.SQLParserFactory;
import com.li.redis.Constant;
import com.li.util.SqlParserTool;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisCluster;

import java.util.Map;

/**
 *
 */
public class CustomizationMonitor<T>  {
    private static final Logger log = LoggerFactory.getLogger(CustomizationMonitor.class);
    private JedisCluster jedisCluster;
    private  T trackLog;


    public CustomizationMonitor(JedisCluster jedisCluster, T trackLog){
        this.jedisCluster = jedisCluster;
        this.trackLog = trackLog;
    }


    /**
     * 根据key, sql_value 生成聚合维度key
     * @param key_head
     * @param sql_rule
     * @param classz
     * @return
     */

    public String execute(String key_head,String sql_rule,Class classz){
        if(StringUtils.isBlank(key_head)){
            log.info("reids 配置key为空 ,key:"+key_head);
            return null;
        }

        if(StringUtils.isBlank(sql_rule)){
            log.info("sql 定制模板配置错误,key:"+key_head);
            return null;
        }
        // sql factory 构建 parse sql 解析器
        SqlParserTool<T> parserTool  = SQLParserFactory.createTrackSqlParserTool(sql_rule,trackLog);
        Map<String,Object> map = parserTool.parserSql();
        if(map==null){
            log.error("sql 语法错误,请重新配置sql模板");
            return null;
        }
        /**2.过滤条件限制*******/
        boolean b = parserTool.checkWhere();
        if(!b){
            log.error("CustomizationMonitor check where error");
            return null;
        }
        /**3.根据GROUP维度生成redis key*******/
        if(map.get(Constant.GROUP)==null){
            log.error("没有可计算的维度");
            return null;
        }
        String dim_key = parserTool.getGroupRedisKey();


        return dim_key.toString();
    }




    public static void main(String[] args) {
        AbstractTrackLog log = new AbstractTrackLog();
        String mm="id=0a6e5ce7f0456c830_0_04\tappid=h5tv\tnewschn=13557\taccounttype=1\tnewsid=\tsubid=\tadslotid=13016\treposition=4\tlc=\trr=31\tos=Android\tosv=6.0.1\tnets=1\tssid=%22ChinaNet-HKp3%22\tbssid=82:4d:8e:b3:71:5c\tcarrierid=1\tappv=5.9.6\tsdkv=1.3.0\tpid=1131\tnewsdkpid=\tidfa=\tidfv=\tadsid=\timei=357556061230231\timsi=460001708207135\tcid=6032007920836063356\tip=10.16.49.178\tlontitude=\tlatitude=31.336625\ttt3=1519394399310\ttt4=\tdelaytrack=0\tappdelaytrack=0\terrorcode=0\tlocal=\tisspam=0\thoursfilt=0\taid=10002768\tcampid=20004652\tadgid=40012153\tcrid=10002422\tpriority=70\tbidtype=2\tbid=71000\tcharge=71000\tnongspcharge=71000\tadgres=4\ttemplateid=7\ttemplate=info_mixpicdownload\tbctimetype=0\ttargettype=4\ttargetsetting=1\tfeaid=0\taudienceid=gender:5066549581250560;business:13510798884339712\tvtag=0\tindustryid=470204\tprectr=0.005592\trankscore=397055.645047\tecpm=397055.645047\tbucket=101_self_rank_67\tbidid=\tdspid=0\tdspsource=\tdelivertype=0\tturn=5\tgeoid1=1156310000\tgeoid2=1156310000\tsource=shjtsybxpsyq\ttimestamp=1519394400177\tett=v\tagrossbudget=19950000000\tadaybudget=200000000000\tcgrossbudget=99999999900000\tcdaybudget=1000000000\tadggrossbudget=99999999900000\tadgdaybudget=1000000000";
        //CustomizationMonitor trend = new CustomizationMonitor(RedisClusterHandler.getInstance(),log.build(mm,""));
        //trend.execute();
    }

    public T getTrackLog() {
        return trackLog;
    }

    public void setTrackLog(T trackLog) {
        this.trackLog = trackLog;
    }
}
