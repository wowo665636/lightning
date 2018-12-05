package com.li.util;

import java.util.Map;

/**
 * map value替换util
 *
 * @author peak
 * @Date 2017年2月13日 下午5:29:34
 */
public class MapUtil {

    /**
     * 遍历替换 target value=null的值，replacement中有则替换
     *
     * @param target      要替换的map
     * @param replacement 替换内容map
     */
    public static void replaceValue(Map<Object, Object> target, Map<String, Object> replacement) {
        for (Map.Entry<Object, Object> entry : target.entrySet()) {
            String key = (String) entry.getKey();
            Object value = entry.getValue();
            Object replaceVal = replacement.get(key);
            if (null == replaceVal) {
                continue;
            }
            if (null == value) {
                entry.setValue(replaceVal);
            }
        }
    }

}
