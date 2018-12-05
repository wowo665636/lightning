package com.li.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.li.redis.Constant;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.*;

/**
 * @Package: com.sohu.util
 * @author: wentaoli214022
 * @date: 2017/10/23 19:10
 * @Des: ... 解密工具类
 */
public class EncryptUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(EncryptUtils.class);
    /**
     * 解密配置文件, JSON 配置字段信息。 加密字段；解密算法参数1；解密算法参数2
     */
    public static final String ENCODE_JSON_FIELD_FLAG = "field";
    public static final String ENCODE_JSON_ALGORITHM_PARA_FIR_FLAG = "algorithmParaFir";
    public static final String ENCODE_JSON_ALGORITHM_PARA_SEC_FLAG = "algorithmParaSec";
    /**
     * 新品算上报日志中的加密字段
     */
    public static final String TOPIC_MILESTONE_RES_ENCODE_FIELD = "viewmonitor";
    /**
     * 解密信息 <TopicName,Fields2DecodeSets>
     */
    private static Map<String, HashSet<String>> decodeMapInfo = new HashMap<String, HashSet<String>>();

    static {
        init();
    }

    /**
     * 初始化解密相关的配置信息
     */
    private static void init() {
        String topicMapDefault = "/decodeInfo.json";
        String JsonContext = null;
        try {
            InputStream inputStream = EncryptUtils.class.getResourceAsStream(topicMapDefault);
            JsonContext = new FileUtil().ReadFile(inputStream);
            inputStream.close();
        } catch (Exception e) {
            LOGGER.error("读取解密配置文件时异常，异常为：", e);
        }
        JSONObject jsonObject = JSONObject.parseObject(JsonContext);
        if (null != jsonObject && jsonObject.size() > 0) {
            Set<String> topicSet = jsonObject.keySet();
            if (null != topicSet && topicSet.size() > 0) {
                Iterator<String> iterator = topicSet.iterator();
                while (iterator.hasNext()) {
                    String topic = iterator.next();
                    JSONArray jsonArray = jsonObject.getJSONArray(topic);
                    if (null != jsonArray && jsonArray.size() > 0) {
                        for (int index = 0; index < jsonArray.size(); index++) {
                            JSONObject currInfo = jsonArray.getJSONObject(index);
                            if (null != currInfo) {
                                if (Constant.TOPIC_MILESTONE_RES.equals(topic)) {
                                    String fieldEncoded = currInfo.getString(ENCODE_JSON_FIELD_FLAG);
                                    if (!StringUtils.isBlank(fieldEncoded)) {
                                        if (!decodeMapInfo.containsKey(topic)) {
                                            decodeMapInfo.put(topic, new HashSet<String>());
                                        }
                                        decodeMapInfo.get(topic).add(fieldEncoded);
                                        String para1 = currInfo.getString(ENCODE_JSON_ALGORITHM_PARA_FIR_FLAG);
                                        String para2 = currInfo.getString(ENCODE_JSON_ALGORITHM_PARA_SEC_FLAG);
                                        if (!StringUtils.isBlank(para1) && !StringUtils.isBlank(para2)) {
                                            CipherUtils.initDecodeParameters(para1, para2);
                                        } else {
                                            LOGGER.error("解析解密配置文件获取主题 " + topic + " 的解密配置时发现参数配置有误，影响解密！！！");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 按照配置信息解密指定主题的所有加密字段
     *
     * @param topic 信息主题
     * @param map   消息内容
     */
    public static void paddingDecodedInfo(final String topic, final Map<Object, Object> map) {
        if (decodeMapInfo.containsKey(topic) && null != map && null != decodeMapInfo.get(topic)) {
            try {
                Set<String> fields2Decode = decodeMapInfo.get(topic);
                String[] arrCurr;
                String[] paramPair;
                String param;
                String value;
                if (Constant.TOPIC_MILESTONE_RES.equals(topic)) {
                    for (String field : fields2Decode) {
                        String encodeStr = (String) map.get(field);
                        if (!StringUtils.isBlank(encodeStr) && TOPIC_MILESTONE_RES_ENCODE_FIELD.equals(field)) {
                            String decodeStr = CipherUtils.urlDecode(encodeStr);
                            if (!StringUtils.isBlank(decodeStr)) {
                                arrCurr = decodeStr.split("\\s");
                                if (null != arrCurr && arrCurr.length > 0) {
                                    int count = arrCurr.length;
                                    for (int index = 0; index < count; index++) {
                                        if (!StringUtils.isBlank(arrCurr[index])) {
                                            paramPair = arrCurr[index].split("=");
                                            param = paramPair[0];
                                            value = paramPair.length == 1 ? "" : paramPair[1];
                                            map.put(param, value);
                                        }
                                    }
                                }
                            }
                            // 解密完加密字段后，移除加密过的字符串，降低网络压力
                            map.remove(field);
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.error("按照配置信息解密指定主题的所有加密字段，发生异常：", e);
            }
        }
    }

    public static void main(String[] args) {
        if (decodeMapInfo.containsKey(null)) {
            System.out.println("--------------------  包含不存在的居然合理");
        } else {
            System.out.println("--------------------  不包含不存在的合理");
        }
    }

}
