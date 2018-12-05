package com.li.test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.reflect.TypeToken;
import com.li.model.HyRoleModel;
import com.li.redis.connect.RedisClient;
import com.li.redis.connect.RedisHandler;
import com.li.storm.bolt.huiyan.LogDealBolt;
import com.li.util.ResultSet2JsonTree;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @Author leizhou215007
 * @Description
 * @date 18:54 2018/6/29
 */
public class Test1 {

    private static final Logger log = LoggerFactory.getLogger(LogDealBolt.class);


    /**
     * 解析计算规则配置为 JSONArray
     */
    private static JSONArray jsonArray;
    static String topicName = "tvadmpv";

    public static void main(String[] args) {
       // getJsonObject(topicName);

        /*Random random = new Random();//指定种子数字
        for (int i=0;i<100;i++){
            System.out.println(random.nextInt(10));
        }*/
        System.out.println("news".equals(null));

    }

    public static void getJsonObject(String topicName) {
        String code = "hy_calculate_role";
        Map<Integer,RedisClient> clientMap = RedisHandler.getInstance();
        int hashCode = Math.abs(code.hashCode())%clientMap.size();
        RedisClient client = clientMap.get(hashCode);
        String json_value =  client.get(code);
        List<HyRoleModel> models =  JSONObject.parseObject(json_value,  new TypeToken<List<HyRoleModel>>(){}.getType());
        log.info(" LogDealBolt 开始 从库中获取计算规则配置，topicName=" + topicName);
        try {
            jsonArray = ResultSet2JsonTree.transform(topicName, models);


            Iterator iterator = jsonArray.iterator();
            while(iterator.hasNext()){
                System.out.println(iterator.next());
            }

            log.info(" LogDealBolt 从库中获取计算规则配置 完成，topicName=" + topicName + "。 计算模型JSON树为：" + jsonArray);
        } catch (Exception e) {
            log.error(" LogDealBolt 从库中获取计算规则配置时 发生异常，topicName=" + topicName + ". 异常为：", e);
        }
    }



}
