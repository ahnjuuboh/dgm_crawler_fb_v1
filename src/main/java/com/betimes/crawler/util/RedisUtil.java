package com.betimes.crawler.util;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

@Component
public class RedisUtil {
    @Value("${redis.server}")
    private String REDIS_HOST;

    private Jedis connect(){
        return new Jedis(REDIS_HOST);
    }

    public void addQueue(String key, String id, String sourceId, String url, String type){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id);
        jsonObject.put("type", type);
        jsonObject.put("source_id", sourceId == null ? null : sourceId);
        jsonObject.put("url", url == null ? null : url);

        Jedis j = this.connect();
        j.rpush(key, jsonObject.toString());
        j.disconnect();
        j = null;
    }
}
