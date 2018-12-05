package com.li.storm.bolt.huiyan;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.reflect.TypeToken;
import com.li.model.HyRoleModel;
import com.li.redis.Constant;
import com.li.redis.connect.RedisClient;
import com.li.redis.connect.RedisHandler;
import com.li.util.CompareUtil;
import com.li.util.EvalUtil;
import com.li.util.ResultSet2JsonTree;
import net.sourceforge.jeval.Evaluator;
import org.apache.commons.lang.StringUtils;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * 数据库读取json配置，分析处理日志
 *
 * @author peak
 * @Date 2017年2月10日 下午6:25:03
 */
public class LogDealBolt extends BaseRichBolt {

    private static final Logger log = LoggerFactory.getLogger(LogDealBolt.class);
    private static final long serialVersionUID = 2127264716886688573L;

    private String topicName;
    private OutputCollector collector;
    /**
     * 计算表达式解析
     */
    private Evaluator evaluator;
    /**
     * 解析计算规则配置为 JSONArray
     */
    private JSONArray jsonArray;
    /**
     * map格式的日志内容
     */
    private Map<Object, Object> map;
    private  Map<Integer,RedisClient> clientMap;
    public LogDealBolt(String topicName) {
        this.topicName = topicName;
    }

    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
        this.getJsonObject(topicName);
        this.evaluator = new Evaluator();
        clientMap = RedisHandler.getInstance();
    }

    public void execute(Tuple input) {
        try {
            map = (Map<Object, Object>) input.getValueByField("map");
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                this.parseNode(jsonObject, new StringBuffer(""), 1.0);
            }
            collector.ack(input);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("LogDealBolt execute 异常。topicName=" + this.topicName + "; 待处理的日志 转为 map=" + map + ";  异常为：",e);
            collector.fail(input);
        }
    }

    /**
     * 数据库中读取过滤逻辑并解析
     */
    public void getJsonObject(String topicName) {
        String code = "hy_calculate_role";
        Map<Integer,RedisClient> clientMap = RedisHandler.getInstance();
        int hashCode = Math.abs(code.hashCode())%clientMap.size();
        RedisClient client = clientMap.get(hashCode);
        String json_value =  client.get(code);
        List<HyRoleModel> models =  JSONObject.parseObject(json_value,  new TypeToken<List<HyRoleModel>>(){}.getType());
        log.info(" LogDealBolt 开始 从库中获取计算规则配置，topicName=" + topicName);
        try {
            jsonArray = ResultSet2JsonTree.transform(topicName, models);
            log.info(" LogDealBolt 从库中获取计算规则配置 完成，topicName=" + topicName + "。 计算模型JSON树为：" + jsonArray);
        } catch (Exception e) {
            log.error(" LogDealBolt 从库中获取计算规则配置时 发生异常，topicName=" + topicName + ". 异常为：", e);
        }
    }

    /**
     * 递归解析 node 根据配置执行逻辑
     *
     * @param jsonObject 当前解析内容
     * @param key        解析的Key
     * @param value      计算累计量
     * @throws Exception
     */
    public void parseNode(JSONObject jsonObject, StringBuffer key, double value) throws Exception {
        // 是否需要拼入 key
        boolean isKey = jsonObject.getBoolean("isKey");
        // 过滤条件通过标识
        boolean isPass = true;
        // 获取该节点的筛选（过滤）逻辑
        JSONArray filters = jsonObject.getJSONArray("filters");
        for (Object object : filters) {
            JSONObject filter = (JSONObject) object;
            // 比较规则 （==, != ,contain ....）
            String fcompare = filter.getString("fcompare");
            // 需要比较的字段
            String fname = filter.getString("fname");
            // 判断对比逻辑
            isPass = CompareUtil.compare(filter.get("fvalue"), map.get(fname), fcompare);
            // 出现 false 直接结束解析当前分支
            if (!isPass) {
                break;
            }
        }
        // 筛选过滤通过
        if (isPass) {
            // 判断该节点是否为指定业务的关键字段，是则将节点关键字拼到Key关键字上
            if (isKey) {
                String keyName = jsonObject.getString("keyName");
                key.append(keyName + "_");
            }
            // 获取计算模式
            String calculateMode = jsonObject.getString("calculateMode");
            // 获取计算公式： 纯数字 或 计算表达式
            String calculate = jsonObject.getString("calculate");
            // 计算公式为空，默认为 1
            if (StringUtils.isBlank(calculate)) {
                calculate = "1";
            } else {
                calculate = EvalUtil.matchReplace(calculate, map);
            }
            double result = Double.parseDouble(this.evaluator.evaluate(calculate));
            // 计算模式解析
            if (StringUtils.isBlank(calculateMode) || Constant.CalMode_Add.equals(calculateMode)) {
                // 计算模式解析， add 模式（默认模式）， add代表在计数时加上 calculate 表达式的值
                double newValue = value * result;
                this.dealSubNodes(jsonObject, key, newValue);
            } else if (Constant.CalMode_Split_Add.equals(calculateMode)) {
                // 计算模式解析，split_add 模式，表示计数时加上指定字段包含的关键字个数
                String[] params = this.splitParam(jsonObject);
                if (params.length > 0) {
                    result *= params.length;
                    double newValue = value * result;
                    this.dealSubNodes(jsonObject, key, newValue);
                }
            } else if (Constant.CalMode_Split_Add_key.equals(calculateMode)) {
                // 计算模式解析，split_add_key 模式，表示计数时分别统计指定字段包含的关键字的个数
                String[] params = this.splitParam(jsonObject);
                double newValue = value * result;
                for (String s : params) {
                    // 进入一层节点时保证都是本层节点状态的 key
                    StringBuffer appendKey = new StringBuffer(key.toString() + "_" + s);
                    this.dealSubNodes(jsonObject, appendKey, newValue);
                }
            }
        }
    }

    /**
     * 处理子节点
     *
     * @param jsonObject 当前解析内容
     * @param key        解析的Key
     * @param value      计算累计量
     * @throws Exception
     */
    private void dealSubNodes(JSONObject jsonObject, StringBuffer key, Double value) throws Exception {
        // 获取子节点
        JSONArray subNodes = jsonObject.getJSONArray("subNodes");
        // 不包含子节点，发射当前 key 并对该key计数
        if (subNodes.size() == 0) {
            //在此处讲key修改掉即可  周雷  20180630
            key.append(map.get("time"));
            collector.emit(new Values(key.toString(), value));
            return;
        }
        // 包含子节点时，遍历子节点
        for (Object object : subNodes) {
            JSONObject subNode = (JSONObject) object;
            // 进入一层节点时保证都是本层节点状态的key
            StringBuffer nodeKey = new StringBuffer(key.toString());
            this.parseNode(subNode, nodeKey, value);
        }
    }

    /**
     * 读取 json 配置中的dealField值查出map中对应的参数值，分隔并返回数组
     */
    private String[] splitParam(JSONObject jsonObject) {
        String[] params = new String[]{};
        String dealField = jsonObject.getString("dealField");
        if (dealField != null) {
            String dealParm = (String) map.get(dealField);
            if (null != dealParm) {
                params = dealParm.split(Constant.Separator);
            }
        }
        return params;
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("key", "value"));
    }

}