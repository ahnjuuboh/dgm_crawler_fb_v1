package com.betimes.crawler.util;

import org.json.JSONObject;

public class JsonUtil {
    public static String getString(JSONObject jsonObject, String key) {
        return (jsonObject.isNull(key)) ? null : jsonObject.getString(key);
    }

    public static Long getLong(JSONObject jsonObject, String key) {
        return (jsonObject.isNull(key)) ? 0 : jsonObject.getLong(key);
    }
}
