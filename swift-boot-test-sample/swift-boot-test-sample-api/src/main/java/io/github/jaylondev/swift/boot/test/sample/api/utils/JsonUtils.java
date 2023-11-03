package io.github.jaylondev.swift.boot.test.sample.api.utils;

import com.alibaba.fastjson.JSONObject;

/**
 * @author jaylon 2023/10/22 11:44
 */
public class JsonUtils {

    public static String toJson(Object obj) {
        return JSONObject.toJSONString(obj);
    }
}
