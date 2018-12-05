package com.li.basic.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * json工具类.
 *
 * @author zhaoshb
 * @since 1.0
 */
public class JsonUtils {

    public static <T> String toJson(T obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return null;
        }

    }
}
