package com.li.util;

import org.apache.commons.lang.StringUtils;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求参数解析工具
 *
 * @author peak
 * @Date 2017年1月18日 上午11:30:33
 */
public class RequestParser {

    /**
     * 解析 url 转换参数为map
     *
     * @param queryString 原始日志内容
     * @param enc         日志编码
     * @return map value 值为数组，如  /xxx?a=1&a=2&b=3   可将a解析为数组
     */
    public static Map<Object, Object> getParamsMap(String queryString, String enc) {
        Map<Object, Object> paramsMap = new HashMap<Object, Object>();
        if (!StringUtils.isBlank(queryString)) {
            String param, value;
            String[] paramPair, values, newValues;
            String[] params;
            params = queryString.split("[&?\\s]");
            for (String subStr : params) {
                paramPair = subStr.split("=");
                param = paramPair[0];
                value = paramPair.length == 1 ? "" : paramPair[1];
                if (paramsMap.containsKey(param)) {
                    values = (String[]) paramsMap.get(param);
                    int len = values.length;
                    newValues = new String[len + 1];
                    System.arraycopy(values, 0, newValues, 0, len);
                    newValues[len] = value;
                } else {
                    newValues = new String[]{value};
                }
                paramsMap.put(param, newValues);
            }
        }
        return paramsMap;
    }

    /**
     * 获得指定名称的参数
     *
     * @param name 参数名称
     * @return
     */
    public static String getParameter(Map<Object, Object> parameterMap, String name) {
        String[] values = (String[]) parameterMap.get(name);
        if (values != null && values.length > 0) {
            return values[0];
        }
        return null;
    }

    /**
     * 获得所有的参数名称
     */
    public static Enumeration getParameterNames(Map<Object, Object> parameterMap) {
        return Collections.enumeration(parameterMap.keySet());
    }

}
