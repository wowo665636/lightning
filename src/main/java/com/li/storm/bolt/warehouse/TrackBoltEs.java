package com.li.storm.bolt.warehouse;

import com.alibaba.fastjson.JSON;
import com.li.core.TdwdStormAppid;
import com.li.es.EsTransportFactory;
import com.li.redis.Constant;
import com.li.util.CommonJsonUtil;
import com.li.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.storm.Config;
import org.apache.storm.Constants;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by wangdi on 18/7/23.
 */
public class TrackBoltEs implements IRichBolt {
    private static final long serialVersionUID = 5306524914701104775L;
    private static final Logger logger = LoggerFactory.getLogger(TrackBoltRedis.class);
    private OutputCollector collector;
    private EsTransportFactory factory = null;

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector = outputCollector;
        try{
              factory = new EsTransportFactory();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void execute(Tuple tuple) {
        try{
            String key  = tuple.getStringByField("key");
            String value_json  = tuple.getStringByField("value");
            //logger.info("message_map:"+ value_json);
            if(StringUtils.isBlank(key)){
                collector.ack(tuple);
                return ;
            }
            if(StringUtils.isBlank(value_json)){
                collector.ack(tuple);
                return ;
            }
            switch (key){
                case "dwd_appid":
                    TdwdStormAppid t_dwd_appid = (TdwdStormAppid) CommonJsonUtil.jsonToUserObject(value_json,TdwdStormAppid.class);
                    if(t_dwd_appid==null){
                        logger.error("t_dwd_appid cover obj is failed,value="+value_json);
                        collector.ack(tuple);
                        return;
                    }
                    DateUtil dateUtil = new DateUtil();
                    String dwd_uid  = t_dwd_appid.getDwd_uid();
                    String md5AsHex = t_dwd_appid.getMd5AsHex();
                    Map<String ,String> esMap  = buildEsMap(dwd_uid);
                    String date_time = dateUtil.timestamp2DateMinute(t_dwd_appid.getTimestamp());
                    String dt =date_time.substring(0,8);
                    String hour = date_time.substring(8,10);
                    String minute = date_time.substring(10,12);
                    esMap.put("sum_charge",String.valueOf(t_dwd_appid.getSum_charge()));
                    esMap.put("count_num",String.valueOf(t_dwd_appid.getCount_num()));
                    esMap.put("minute",minute);
                    esMap.put("hour",hour);
                    esMap.put("dt",dt);
                    factory.bulkRequest.add(factory.transportClient.prepareIndex(Constant.T_XPS2_TRACK_STORM_ADSLOTID, "my_type", md5AsHex).setSource(esMap));
                    break;
                default:
                    System.out.println("default ~~~~");

            }
            long size = factory.bulkRequest.request().requests().size();
            if (size % 1000==0){
                System.out.println("bulk request  is submit ,tuple:"+ JSON.toJSONString(tuple));
                factory.bulkRequest.execute().actionGet();
                System.out.println("---------------- bulkSize:"+factory.bulkRequest.request().requests().size());
                factory.bulkRequest = factory.getTransportClient().prepareBulk();
                //factory.init();
                collector.ack(tuple);
                return ;
            }


        }catch (Exception e){
            e.printStackTrace();
            logger.error(" TrackBoltEs exception:",e);

        }
        collector.ack(tuple);
        return ;
    }

    /**
     * 生成es 可用map
     * @param uniqueKey
     * @return
     */
    public Map<String,String> buildEsMap(String uniqueKey){
        if(StringUtils.isBlank(uniqueKey)){
            logger.error("buildEsMap param is null");
            return null;
        }
        Map<String,String> esMap = new LinkedHashMap<String, String>();
        String[] str_arr = uniqueKey.split(Constant.DIM_TUPLE);
        if(str_arr.length==0){
            logger.error("buildEsMap uniqueKey format is error");
            return null;
        }
        for(String field:str_arr){
            String[] field_arr = field.split(Constant.SP_FILED);
            if(field_arr.length==2){
                esMap.put(field_arr[0],field_arr[1]);
            }else {
                esMap.put(field_arr[0],"");
            }

        }
        return esMap;
    }

    /**
     * 数据导入es
     * @param esMap
     * @param esTable
     * @param dateTime
     * @param md5AsHex
     */
    private void map2es( Map<String ,String> esMap,String esTable,String dateTime,String md5AsHex){
        EsTransportFactory es_client = new EsTransportFactory(esTable,dateTime);
        try{
            TransportClient transportClient = es_client.getTransportClient();
            BulkRequestBuilder bulkRequest = transportClient.prepareBulk();
            esMap.put("dt",dateTime);
            bulkRequest.add(transportClient.prepareIndex(esTable, "my_type", md5AsHex).setSource(esMap));
            bulkRequest.execute().actionGet();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            es_client.close();
        }

    }

    /**
     * 根据传送过来的Tuple，判断本Tuple是否是tickTuple 如果是tickTuple，则触发动作
     *
     * @param tuple
     * @return
     */
    public static boolean isTickTuple(Tuple tuple) {
        return tuple.getSourceComponent().equals(Constants.SYSTEM_COMPONENT_ID) // SYSTEM_COMPONENT_ID
                && tuple.getSourceStreamId().equals(Constants.SYSTEM_TICK_STREAM_ID); // SYSTEM_TICK_STREAM_ID

    }


    @Override
    public void cleanup() {
        logger.info("TrackBoltEs is close");
        factory.bulkRequest.execute().actionGet();
        factory.close();
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("message_es"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {

//        Config conf = new Config();
//        conf.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS,1);
//        return conf;
        return null;
    }
}
