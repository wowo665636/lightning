package com.li.storm.bolt.sstics;

import com.li.core.AbstractTvLog;
import com.li.es.EsTransportFactory;
import com.li.redis.Constant;
import com.li.redis.connect.RedisClient;
import com.li.redis.connect.RedisHandler;
import com.li.util.CommonJsonUtil;
import com.li.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by wangdi on 18/7/2.
 */
public class TvRedisBolt extends AbstractTvLog implements IRichBolt {
    private static final Logger log = LoggerFactory.getLogger(TvRedisBolt.class);
    private static final long serialVersionUID = -6126507826933540640L;
    private OutputCollector collector;
    private static  Map<Integer,RedisClient> clientMap;
    private final static String esTable = Constant.T_BI_TV_DETAIL;

    private Map<String,String> columnMap = new HashMap<String,String>();

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        columnMap.put("plat","plat");
        columnMap.put("adstyle","adstyle");
        columnMap.put("bk","bk");
        columnMap.put("pointtime","pointtime");

        this.collector = outputCollector;
        try{
            clientMap = RedisHandler.getInstance();
        }catch (Exception e){
            log.error("jedis is null");
        }
    }

    @Override
    public void execute(Tuple tuple) {


        try{
            String message = (String)tuple.getValue(0);
            //log.info("dim_message:"+message);
            if(StringUtils.isEmpty(message)){
                log.error("TvRedis2EsBolt message is null");
                collector.ack(tuple);
                return;
            }
            AbstractTvLog tvLog = (AbstractTvLog)CommonJsonUtil.jsonToUserObject(message,AbstractTvLog.class);
            if(tvLog==null){
                log.error("tvLog  is null");
                collector.ack(tuple);
                return;
            }
            String dateTime = DateUtil.timestamp2Day(tvLog.getTime());
            if(StringUtils.isBlank(dateTime)){
                log.error("date_time  is null");
                collector.ack(tuple);
                return;
            }
            String uniqueKey =  tvLog.uniqueKey();
            int hashCode = Math.abs(uniqueKey.hashCode())%clientMap.size();
            RedisClient client = clientMap.get(hashCode);
            Long count_vv = client.hincrBy(Constant.T_BI_TV_DETAIL+dateTime,uniqueKey,1); // 按分钟累计
            redis2es(tvLog,count_vv,dateTime);
        }catch (Exception e){
            log.error("execute exception :",e);
        }
        collector.ack(tuple);

    }

    /**
     * countvv 写入es
     * @param tvLog
     * @param count_vv
     * @param dateTime
     */
    private void redis2es(AbstractTvLog tvLog,Long count_vv, String dateTime){
        EsTransportFactory es_client = new EsTransportFactory(esTable,dateTime);
        try{
            TransportClient transportClient = es_client.getTransportClient();
            BulkRequestBuilder bulkRequest = transportClient.prepareBulk();
            Map<String, String> cellMap = new LinkedHashMap<String, String>();
            Field[] fields = tvLog.getClass().getDeclaredFields();
            for(Field field :fields){
                field.setAccessible(true);
                String column =  field.getName();
                if(columnMap.containsKey(column)){
                    Object obj = field.get(tvLog);
                    cellMap.put(column,obj==null?"":obj.toString());
                }
            }
            cellMap.put("count_vv",String.valueOf(count_vv));
            cellMap.put("dt",dateTime);
            bulkRequest.add(transportClient.prepareIndex(esTable, "my_type", tvLog.getMD5Hash()).setSource(cellMap));
            bulkRequest.execute().actionGet();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            es_client.close();
        }

    }


    @Override
    public void cleanup() {

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("model", "count"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
