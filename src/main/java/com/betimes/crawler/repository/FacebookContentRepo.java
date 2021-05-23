package com.betimes.crawler.repository;

import com.betimes.crawler.model.FacebookContent;
import com.betimes.crawler.util.HttpUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FacebookContentRepo {
    @Value("${config.es.search.id}")
    private String ES_SEARCH_BY_ID_URL;
    @Value("${config.es.index}")
    private String INDEX;

    private String DOC_TYPE = "json";

    public void save(FacebookContent facebookContent) {
        String url = this.ES_SEARCH_BY_ID_URL.replaceAll("@index", this.INDEX)
                .replaceAll("@type", this.DOC_TYPE).replaceAll("@id", facebookContent.getId());
        try {
            JSONObject jsonBody = new JSONObject(facebookContent);
            HttpUtil.doPost(url, jsonBody.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
