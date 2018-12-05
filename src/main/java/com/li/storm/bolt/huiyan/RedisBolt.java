package com.li.storm.bolt.huiyan;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.stat.TableStat;
import com.li.redis.Constant;
import com.li.redis.connect.RedisClient;
import com.li.redis.connect.RedisHandler;
import com.li.util.SqlParserTool;
import org.apache.commons.lang.StringUtils;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存计数操作
 *
 * @author peak
 * @Date 2017年2月10日 下午6:26:47
 */
public class RedisBolt extends BaseRichBolt {

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(RedisBolt.class);


    private OutputCollector collector;
    private Map<Integer,RedisClient> clientMap;
    private Map<String,String> mapWhere;
    private Set<String> groupSet;

    public RedisBolt() {

    }

    public void initParams(){
        String sql = "SELECT sum(pv),sum(vv),sum(reg_pv) from tv_table where appid in ('tv') and ssid in ('pc','wap','app','ott') and plat in('ALL','ipad','gpad','iphone','gphone') and adstyle in ('oad','mad','ead','wrapframe','pad','flogo','fbarad','barrage','page','open','focus','overfly','mp') group by appid,ssid,plat,adstyle";
        Map<String,Object> map = null;//SqlParserTool.parserSql();
        mapWhere = new HashMap<String, String>();

        List<SQLSelectItem> selects = (List<SQLSelectItem>) map.get(Constant.SELECT);
        for(SQLSelectItem select:selects){
            mapWhere.put(String.valueOf(select.getExpr().getChildren().get(0)),"$tp");
        }

        List<TableStat.Condition> fields = (List<TableStat.Condition>) map.get(Constant.WHERE);
        for(TableStat.Condition column:fields){
            String name = column.getColumn().getName();
            List<Object> values = column.getValues();
            for(Object obj:values){
                mapWhere.put(obj.toString(),name);
            }
        }

        Set<TableStat.Column> groups = (Set<TableStat.Column>) map.get(Constant.GROUP);
        groupSet = new HashSet<>();
        for(TableStat.Column column:groups){
            groupSet.add(column.getName());
        }
    }

    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
        clientMap = RedisHandler.getInstance();
        initParams();
    }

/*    public void execute(Tuple input) {
        try {
           String hkey="hy_test";

            RedisClient client = clientMap.get(0);
            String key = input.getStringByField("key");
            Double temp = input.getDoubleByField("value");
            Double value = 0.0;

            if (StringUtils.isNotBlank(key)) {
                System.out.println("--key:"+key);
                client.hIncrByFloat(hkey,key, temp+value);
                client.expire(hkey,60*60);
            }
            this.collector.ack(input);
        } catch (Exception e) {
            this.collector.fail(input);
            log.error("RedisBolt execute occurs exception.   -----  It is:", e);
        }
    }*/

    public void execute(Tuple input) {
        try {
            String hkey="hy_test_";

            RedisClient client = clientMap.get(0);
            String key = input.getStringByField("key");

            Map<String,String> map = keyTransform(key);
            String type = map.get("type");
            String newKey = map.get("newKey");
            hkey+=type;

            Double temp = input.getDoubleByField("value");
            Double value = 0.0;

            if (StringUtils.isNotBlank(newKey)) {
                System.out.println("key:"+key+"              ,newkey:"+newKey);
                client.hIncrByFloat(hkey,newKey, temp+value);
                client.expire(hkey,60*60);
            }
            this.collector.ack(input);
        } catch (Exception e) {
            this.collector.fail(input);
            log.error("RedisBolt execute occurs exception.   -----  It is:", e);
        }
    }


    public Map<String,String> keyTransform(String key){
        log.info("before keyTransform key="+key);
        Map<String,String> result = new HashMap<>();
        String[] keyarr = key.split("_");
        if (!keyarr[0].equals("tv")){
            keyarr = ("tv_"+key).split("_");
        }

        String newKey = "";
        String type="";
        for(String k:keyarr){
            String colname = mapWhere.get(k);
            if(colname!=null){
                if(colname.equals("$tp")){
                    type=k;
                }else if(groupSet.contains(colname)){
                    newKey+=colname+"="+k+"&";
                }
            }else{
                newKey+="dtime="+k;
            }
        }

        log.info("after keyTransform key="+newKey+"            ,type="+type);
        result.put("type",type);
        result.put("newKey",newKey);
        return result;
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {

    }



    public void cleanup() {
        log.info("RedisBolt cleanup processing starts.");
        log.info("RedisBolt cleanup processing terminates.");
    }

}