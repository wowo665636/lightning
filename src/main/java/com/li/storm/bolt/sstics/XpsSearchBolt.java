package com.li.storm.bolt.sstics;

import com.li.core.AbstractSearchLog;
import com.li.core.SQLParserFactory;
import com.li.redis.Constant;
import com.li.redis.cluster.RedisClusterHandler;
import com.li.util.SqlParserTool;
import org.apache.commons.lang.StringUtils;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import redis.clients.jedis.JedisCluster;

import java.util.Map;

/**
 * Created by wangdi on 18/4/2.
 */
public class XpsSearchBolt extends AbstractSearchLog implements IRichBolt {
    private static final long serialVersionUID = 8509019957246162793L;

    private OutputCollector collector;
    private static JedisCluster jedisCluster;
    private static final int EXPIRE_TIME = 24*60*60;

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector = outputCollector;
        try{
            jedisCluster = RedisClusterHandler.getInstance();
        }catch (Exception e){
            System.err.println("jedis is null");
        }
    }

    @Override
    public void execute(Tuple tuple) {
        String message = (String)tuple.getValue(4);
        System.out.println("message:"+message);

        if(StringUtils.isBlank(message)){
            System.err.println("--kafka message is null---:"+message);
            collector.ack(tuple);
            return ;
        }

        try{

            AbstractSearchLog searchLog = super.build(message, Constant.SP_BLANK);
            if(!checkBusinessData(searchLog)){
                System.err.println("--kafka search message error---:"+message);
                collector.ack(tuple);
                return ;
            }

            if(!StringUtils.isBlank(searchLog.getCrid())){
                System.err.println("此数据不是空广告,不统计---crid:"+searchLog.getCrid());
                collector.ack(tuple);
                return ;
            }

           // String day_hour = searchLog.getCurrentTime().substring(0,10);
            String day_key = searchLog.getCurrentTime().substring(0,8);


            String sql_rule = jedisCluster.get(Constant.SQL_RULE_SEARCH);
            SqlParserTool<AbstractSearchLog> parserTool  = SQLParserFactory.createSearchSqlParserTool(sql_rule,searchLog);
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

            //**search 上报的空广告
            jedisCluster.hincrBy(Constant.BI_SEARCH_+Constant.NA_+day_key,dim_key.toString(),1);
            jedisCluster.expire(Constant.BI_SEARCH_+Constant.NA_+day_key,EXPIRE_TIME);
            collector.ack(tuple);
            return;


        }catch (Exception e){
            e.printStackTrace();
            System.out.println("search execute exception");
            collector.ack(tuple);
            return ;
        }

    }

    @Override
    public void cleanup() {
        System.out.println("XpsSearchBolt is close");

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("message"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
