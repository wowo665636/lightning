package com.li.util;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Evaluator util
 *
 * @author peak
 * @Date 2017年2月28日 下午3:42:28
 */
public class EvalUtil {

    /**
     * 匹配并替换，未匹配到的变量直接用1替换，匹配模式为 #{variableName}
     */
    public static String matchReplace(String calculate, Map<Object, Object> map) {
        Pattern p = Pattern.compile("#\\{(\\w+)\\}");
        Matcher m = p.matcher(calculate);
        while (m.find()) { //取出表达式中变量并替换为真实的值
            String mkey = m.group(1);
            Object mvalue = map.get(mkey);
            if (null == mvalue) {
                mvalue = 1;
            }
            calculate = calculate.replace("#{" + mkey + "}", mvalue.toString());
        }
        return calculate;
    }

}
