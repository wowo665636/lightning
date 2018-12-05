package com.li.storm.bolt.sstics;


import com.li.core.AbstractTrackLog;
import com.li.core.SQLParserFactory;
import com.li.redis.Constant;
import com.li.util.DateUtil;
import com.li.util.SqlParserTool;
import redis.clients.jedis.JedisCluster;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by wangdi on 18/2/24.
 */
public class TrackTrendMonitor implements Serializable {
    private static final long serialVersionUID = 1189809797603981581L;

    private JedisCluster jedisCluster;
    private AbstractTrackLog trackLog;
    private static final int EXPIRE_TIME = 24*60*60;

    public TrackTrendMonitor(JedisCluster jedisCluster, AbstractTrackLog trackLog){
        this.jedisCluster = jedisCluster;
        this.trackLog = trackLog;
    }
    public void execute(){
        String id = trackLog.getId();
        String timestamp = trackLog.getTimestamp();
        String ett= trackLog.getEtt();
        /**过滤重复上报数据*/

       // ByteUtils byteUtils = new ByteUtils();
        //long long_key = byteUtils.bytesToLong(repeat_key.getBytes()).getLong();
        //String long_redis_key = String.valueOf(long_key);
        /*
        StringBuilder sb = new StringBuilder();
        String repeat_key = sb.append(id).append("_").append(ett).append("_").append(timestamp).append("repeat"+Constant.BI_TREND_).toString();
        long count =  jedisCluster.incr(repeat_key);
        jedisCluster.expire(repeat_key,60);
        if(count>1){
            System.err.println("重复数据,不做业务处理. "+repeat_key);
            return ;
        }*/
        DateUtil dateUtil = new DateUtil();

        String day_hour =  dateUtil.timestamp2Hour(timestamp);

        /**1.解析sql**/
       // Class classz = trackLog.getClass();
        String sql_rule = jedisCluster.get(Constant.SQL_RULE_TREND);
        SqlParserTool<AbstractTrackLog> parserTool  = SQLParserFactory.createTrackSqlParserTool(sql_rule,trackLog);

        Map<String,Object> map = parserTool.parserSql();
        if(map==null){
            System.err.println("sql 语法错误,请重新配置sql模板");
            return ;
        }
        boolean b = parserTool.checkWhere();
        if(!b){
            //System.err.println("不满足where 过滤条件:id="+id);
            return ;
        }

        /**3.根据GROUP维度生成redis key*******/
        if(map.get(Constant.GROUP)==null){
            System.err.println("SQL模板没有配置可计算的维度");
            return ;
        }
        String dim_key = parserTool.getGroupRedisKey();
        /**4.计算 加载/展示/点击/收入*****/

        switch (ett){
            case "v":
                //System.out.println(Constant.BI_TREND_V_+day_hour+","+dim_key.toString());
                jedisCluster.hincrBy(Constant.BI_TREND_V_+day_hour,dim_key.toString(),1);
                jedisCluster.expire(Constant.BI_TREND_V_+day_hour,EXPIRE_TIME);
                /**4.计算收入  cpm**/
                if(trackLog.checkIncome(trackLog)&&"2".equals(trackLog.getBidtype())) {
                    jedisCluster.hincrBy(Constant.BI_TREND_CONSUME_ + day_hour, dim_key.toString(), Long.valueOf(trackLog.getCharge()));
                    jedisCluster.expire(Constant.BI_TREND_CONSUME_ + day_hour, EXPIRE_TIME);
                }
                break;
            case "av":
                if(!trackLog.checkAcIncome(trackLog)){
                    break;
                }
                // System.out.println(Constant.BI_TREND_AV_+day_hour+","+dim_key.toString());
                jedisCluster.hincrBy(Constant.BI_TREND_AV_+day_hour,dim_key.toString(),1);
                jedisCluster.expire(Constant.BI_TREND_AV_+day_hour,EXPIRE_TIME);

                if(trackLog.checkIncome(trackLog)&&"4".equals(trackLog.getBidtype())){
                    jedisCluster.hincrBy(Constant.BI_TREND_CONSUME_ + day_hour, dim_key.toString(), Long.valueOf(trackLog.getCharge()));
                    jedisCluster.expire(Constant.BI_TREND_CONSUME_ + day_hour, EXPIRE_TIME);
                }
                break;
            case "click":
                // System.out.println(EttEnum.CLICK);
                // System.out.println(Constant.BI_TREND_C_+day_hour+","+dim_key.toString());
                jedisCluster.hincrBy(Constant.BI_TREND_C_+day_hour,dim_key.toString(),1);
                jedisCluster.expire(Constant.BI_TREND_C_+day_hour,EXPIRE_TIME);
                /**4.计算收入  cpc**/
                if(trackLog.checkIncome(trackLog)&&"3".equals(trackLog.getBidtype())){
                    jedisCluster.hincrBy(Constant.BI_TREND_CONSUME_ + day_hour, dim_key.toString(), Long.valueOf(trackLog.getCharge()));
                    jedisCluster.expire(Constant.BI_TREND_CONSUME_ + day_hour, EXPIRE_TIME);
                }
                break;
            default:
                System.out.println("此类型日志,暂不出处理,ett="+ett);
        }
       // System.out.println(Constant.BI_TREND_V_+day_hour+","+dim_key.toString());
       // System.out.println(Constant.BI_TREND_CONSUME_+day_hour+","+dim_key.toString());
       // System.out.println("adgres:"+trackLog.getAdgres()+",ett:"+ trackLog.getEtt()+",bidtype:"+ trackLog.getBidtype());
        return;
    }



    public static void main(String[] args) {
        AbstractTrackLog log = new AbstractTrackLog();
        String mm="id=0a6e5ce7f0456c830_0_04\tappid=h5tv\tnewschn=13557\taccounttype=1\tnewsid=\tsubid=\tadslotid=13016\treposition=4\tlc=\trr=31\tos=Android\tosv=6.0.1\tnets=1\tssid=%22ChinaNet-HKp3%22\tbssid=82:4d:8e:b3:71:5c\tcarrierid=1\tappv=5.9.6\tsdkv=1.3.0\tpid=1131\tnewsdkpid=\tidfa=\tidfv=\tadsid=\timei=357556061230231\timsi=460001708207135\tcid=6032007920836063356\tip=10.16.49.178\tlontitude=\tlatitude=31.336625\ttt3=1519394399310\ttt4=\tdelaytrack=0\tappdelaytrack=0\terrorcode=0\tlocal=\tisspam=0\thoursfilt=0\taid=10002768\tcampid=20004652\tadgid=40012153\tcrid=10002422\tpriority=70\tbidtype=2\tbid=71000\tcharge=71000\tnongspcharge=71000\tadgres=4\ttemplateid=7\ttemplate=info_mixpicdownload\tbctimetype=0\ttargettype=4\ttargetsetting=1\tfeaid=0\taudienceid=gender:5066549581250560;business:13510798884339712\tvtag=0\tindustryid=470204\tprectr=0.005592\trankscore=397055.645047\tecpm=397055.645047\tbucket=101_self_rank_67\tbidid=\tdspid=0\tdspsource=\tdelivertype=0\tturn=5\tgeoid1=1156310000\tgeoid2=1156310000\tsource=shjtsybxpsyq\ttimestamp=1519394400177\tett=v\tagrossbudget=19950000000\tadaybudget=200000000000\tcgrossbudget=99999999900000\tcdaybudget=1000000000\tadggrossbudget=99999999900000\tadgdaybudget=1000000000";
    }

    public AbstractTrackLog getTrackLog() {
        return trackLog;
    }

    public void setTrackLog(AbstractTrackLog trackLog) {
        this.trackLog = trackLog;
    }

    public JedisCluster getJedisCluster() {
        return jedisCluster;
    }

    public void setJedisCluster(JedisCluster jedisCluster) {
        this.jedisCluster = jedisCluster;
    }

}
