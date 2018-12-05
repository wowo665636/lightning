package com.li.redis.connect;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.reflect.TypeToken;
import com.li.model.HyRoleModel;
import com.li.redis.Constant;
import com.li.util.CompareUtil;
import com.li.util.EvalUtil;
import com.li.util.ResultSet2JsonTree;
import net.sourceforge.jeval.Evaluator;
import org.apache.commons.lang.StringUtils;
import org.apache.storm.tuple.Values;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by wangdi on 18/5/28.
 */
public class TestStormMain {
    private Evaluator evaluator = new Evaluator();
    private static JSONArray jsonArray;
    private Map<Object, Object> map;
    public static void main(String[] args) throws Exception{
        String code = "hy_test";
        //String code="my_test_1";
        Map<Integer,RedisClient> clientMap = RedisHandler.getInstance();
        int hashCode = Math.abs(code.hashCode())%clientMap.size();
        RedisClient client = clientMap.get(hashCode);

        String exp = "2 + (7-5) * 3.14159 * #{x} + sin(0)";

       // Evaluator jevalEvaluator = new Evaluator();
        //jevalEvaluator.setVariables(Collections.singletonMap("x", Double.toString(20)));

        //double result = Double.parseDouble(jevalEvaluator.evaluate(exp));
        //client.hIncrByFloat(code,"m1",result);
        Map<String,String> map = client.hgetAll(code);
        System.out.println(JSON.toJSONString(map));
       // System.out.println(result);//-> 2.0
        /*int hashCode = Math.abs(code.hashCode());
        System.out.println(hashCode);
        Set<Integer> set = new HashSet<>();
        for(int i=0;i<100;i++){
            set.add(i%3);
        }
        System.out.println(JSON.toJSONString(set));
        System.out.println(hashCode%20);*/

    }

    public static void testTopo(){
        try{
            StringBuilder sb = new StringBuilder();
            FileReader reader = new FileReader("hy_role.txt");
            BufferedReader br = new BufferedReader(reader);
            String str ="";
            List<HyRoleModel> list = new ArrayList<>(1400);
            while ( (str = br.readLine())!=null){
                String[] line_arr = str.split("\t");
                HyRoleModel model = new HyRoleModel();
                model.setId(line_arr[0]);
                model.setKey_name(line_arr[1]);
                model.setParent_id(line_arr[2]);
                model.setCalculate_mode(line_arr[3]);
                model.setCalculate(line_arr[4]);
                model.setDeal_field(line_arr[5]);
                model.setFilters(line_arr[6]);
                model.setIs_key(line_arr[7]);
                list.add(model);
            }
            String value = JSON.toJSONString(list);
            String code = "hy_calculate_role";
            Map<Integer,RedisClient> clientMap = RedisHandler.getInstance();
            int hashCode = Math.abs(code.hashCode())%clientMap.size();
            RedisClient client = clientMap.get(hashCode);
            //  client.set(code,value);
            // String json_value = client.get(code);
            //List<HyRoleModel> models =  JSONObject.parseObject(json_value,  new TypeToken<List<HyRoleModel>>(){}.getType());
            TestStormMain main = new TestStormMain();
            main.getJsonObject("tvadmpv");
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                main.parseNode(jsonObject, new StringBuffer(""), 1.0);
            }
            //client.expire(code,60);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void getJsonObject(String topicName) {
        String code = "hy_calculate_role";
        Map<Integer,RedisClient> clientMap = RedisHandler.getInstance();
        int hashCode = Math.abs(code.hashCode())%clientMap.size();
        RedisClient client = clientMap.get(hashCode);
        String json_value =  client.get(code);
        List<HyRoleModel> models =  JSONObject.parseObject(json_value,  new TypeToken<List<HyRoleModel>>(){}.getType());
        System.out.println(" LogDealBolt 开始 从库中获取计算规则配置，topicName=" + topicName);
        try {
            jsonArray = ResultSet2JsonTree.transform(topicName, models);
            System.out.println(" LogDealBolt 从库中获取计算规则配置 完成，topicName=" + topicName + "。 计算模型JSON树为：" + jsonArray);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(" LogDealBolt 从库中获取计算规则配置时 发生异常，topicName=" + topicName + ". 异常为：");
        }
    }

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

            double result = Double.parseDouble(evaluator.evaluate(calculate));
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
            key.append(map.get("time"));
            System.out.println(new Values(key.toString(), value));
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

}
