package com.li.util;

import java.util.regex.Pattern;

/**
 * Created by wangdi on 18/5/15.
 */
public class EtlUtils {

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }
}
