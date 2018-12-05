package com.li.storm.bolt.huiyan;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.li.redis.Constant;
import com.li.redis.connect.RedisClient;
import com.li.util.*;
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

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 负责原始日志解析，解析结果格式为 Map
 *
 * @author peak
 * @Date 2017年2月10日 下午6:24:14
 */
public class LogParseBolt extends BaseRichBolt {

    private static final Logger log = LoggerFactory.getLogger(LogParseBolt.class);
    private static final long serialVersionUID = 3584455337784330548L;
    private OutputCollector collector;
    private Map<Object, Object> map;
    private String topicName;
    private String parseType;
    private Date date = new Date();
    private JSONObject jsonObject; //配置的默认值


    public LogParseBolt(String topic, String type) {
        this.topicName = topic;
        // 优先根据输入参数获取数据源数据类型
        this.parseType = type;
        if (StringUtils.isBlank(this.parseType) || (!Constant.Parse_Json.equals(this.parseType) && !Constant.Parse_Url.equals(this.parseType))) {
            // 根据 topic 选择不同的解析方式
            this.parseType = Constant.parseMap.get(this.topicName);
        }
    }

    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
        // 读取配置文件，获取 json 格式的默认值，并解析成 JSONObject
        String topicMapDefault = "/mapDefault.json";
        InputStream inputStream = LogDealBolt.class.getResourceAsStream(topicMapDefault);
        String JsonContext = new FileUtil().ReadFile(inputStream);
        jsonObject = JSONObject.parseObject(JsonContext);
    }

    public void execute(Tuple input) {
        String msg = null;
        try {
            msg = (String)input.getValue(4);
            // 根据数据源数据类型解析日志，解析为 Map 格式
            if (Constant.Parse_Json.equals(parseType)) {
                this.parseJson(msg);
            } else if (Constant.Parse_Url.equals(parseType)) {
                this.parseUrl(msg);
            } else {
                map = null;
                log.error("粘贴格式有误，既不是JSON也不是URL。");
            }
            if (null != map) {
                // 解析后 map 处理（替换为默认值）
                MapUtil.replaceValue(map, jsonObject);
                collector.emit(new Values(map));
            }
            collector.ack(input);
        } catch (Exception e) {
            collector.fail(input);
            log.error("LogParseBolt execute 异常。topic=" + this.topicName + "; msg=" + msg + "。 异常为: ", e);
        }
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("map"));
    }

    /**
     * json 日志解析方法
     */
    private void parseJson(String msg) {
        // STEP-1 : 解析日志为 map
        msg = msg.substring(msg.indexOf("{"));
        map = (Map<Object, Object>) JSON.parse(msg);
        // STEP-2 : 解析加密字段
        EncryptUtils.paddingDecodedInfo(topicName, map);
        // STEP-3 :  时间字段填充，对于JSON格式的请求，此处使用日志打印时间
        long time = Long.parseLong(map.get("TIME") + "") * 1000;
        date.setTime(time);
        String timeStr = DateUtil.dateTimeToString(date);
        map.put("time", timeStr);
    }

    /**
     * url请求解析方式
     */
    private void parseUrl(String msg) throws Exception {
        // STEP-1 : 解析原始日志，转化为 Map<String,Sting[]> 格式
        map = RequestParser.getParamsMap(msg, null);
        // STEP-2 : Map内容格式转换， Map<String,Sting[]> 转化为 Map<String,Sting>。  因为工具类产生的 map 的 value 值为数组，此处遍历替换为第一个元素
        String key;
        String value;
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            key = (String) entry.getKey();
            value = RequestParser.getParameter(map, key);
            entry.setValue(value);
        }
        // STEP-3 : 解析加密字段
        EncryptUtils.paddingDecodedInfo(topicName, map);
        // STEP-4 : 解析时间字段，对于URL格式的请求，使用日志打印时间作为时间字段
        Pattern p = Pattern.compile("\\[(.*) \\+0800\\]");
        Matcher m = p.matcher(msg);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss", Locale.ENGLISH);
        long time = 0L;
        if (m.find()) {
            String tempStr = m.group(1);
            time = sdf.parse(tempStr).getTime();
        }
        date.setTime(time);
        String timeStr = DateUtil.dateTimeToString(date);
        map.put("time", timeStr);
    }

}