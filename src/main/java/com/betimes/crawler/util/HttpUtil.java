package com.betimes.crawler.util;

import com.betimes.crawler.exception.MyHttpException;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

public class HttpUtil {
    public static String doGet(String url) throws MyHttpException, Exception {
        HttpResponse<JsonNode> response = Unirest.get(url)
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .asJson();

        if (response.getStatus() != 200){
            throw new MyHttpException(response.getStatus(), response.getBody().toString());
        }
        return response.getBody().toString();
    }

    public static String doPost(String url, String json) throws MyHttpException, Exception {
        HttpResponse<JsonNode> response = Unirest.post(url)
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .body(json).asJson();

        if (response.getStatus() != 200 && response.getStatus() != 201){
            throw new MyHttpException(response.getStatus(), response.getBody().toString());
        }
        return response.getBody().toString();
    }
}
