package com.li.storm.bolt.warehouse;

import com.alibaba.fastjson.JSON;
import com.li.core.AbstractTrackLog;
import com.li.core.TdwdStormAppid;
import com.li.redis.Constant;
import com.li.redis.connect.RedisClient;
import com.li.redis.connect.RedisHandler;
import com.li.util.CaBaseConver;
import com.li.util.CommonJsonUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by wangdi on 18/7/23.
 */
public class TrackBoltDwd extends AbstractTrackLog implements IRichBolt {
    private static final long serialVersionUID = 1960504261081007762L;
    private static final Logger logger = LoggerFactory.getLogger(TrackBoltDwd.class);
    private OutputCollector collector;

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector = outputCollector;
    }

    /**
     * 根据redis 配置过滤规则,生成主题表
     * @param tuple
     */
    @Override
    public void execute(Tuple tuple) {
        try{

            String message = (String)tuple.getValue(4);
            //logger.info("message:"+message);
            if(StringUtils.isBlank(message)){
                collector.ack(tuple);
                return ;
            }
            /**1.初始 track model*/
            AbstractTrackLog trackLog = super.build(message,super.sp_tuple);
            /**过滤作弊数据,暂时不加,保持跟离线数据一致*/
            if (super.checkIsSpam(trackLog)){
                collector.ack(tuple);
                return ;
            }
            if(!checkBusinessData(trackLog)){
                collector.ack(tuple);
                return ;
            }
           /* Random random = new Random();
            int client_key = random.nextInt(clientMap.keySet().size());
            RedisClient client = clientMap.get(client_key);
            Map<String, String>  rule_map = client.hgetAll(Constant.BI_CUSTOM_2_RULE);
            if(rule_map==null){
                logger.error("尚未定制模板,请配置sql模板");
                collector.ack(tuple);
            }*/
            TdwdStormAppid dwd_appid = new TdwdStormAppid();
            CaBaseConver.copy(trackLog ,AbstractTrackLog.class ,dwd_appid,TdwdStormAppid.class);
            if(dwd_appid!=null){
                String dwd_appid_json = JSON.toJSONString(dwd_appid);
                /**每个宽表都命名一个key*/
                collector.emit(new Values("dwd_appid",dwd_appid_json));//
            }



        }catch (Exception e){
            logger.error("TrackBoltDwd exception: ");
            e.printStackTrace();

        }
        collector.ack(tuple);
        return;
    }




    @Override
    public void cleanup() {
        logger.info("TrackBoltDwd is close");
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("key", "value"));

    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
