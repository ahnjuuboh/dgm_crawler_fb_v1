package com.betimes.crawler.service;

import com.betimes.crawler.exception.MyHttpException;
import com.betimes.crawler.model.FbProfile;
import com.betimes.crawler.repository.FbProfileRepo;
import com.betimes.crawler.util.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class FbProfileService {
    private static final Logger log = LoggerFactory.getLogger(FbProfileService.class);

    @Autowired
    private FbProfileRepo fbProfileRepo;
    @Autowired
    private RedisUtil redisUtil;

    @Value("${app.token}")
    private String appToken;
    @Value("${config.fb.page.info.url}")
    private String pageInfoUrl;
    @Value("${config.redis.facebook.page.key}")
    private String redisPageKey;

    public void fetchInfo(String id) throws Exception {
        log.info(id + "|fetchInfo|Start");
        try {
            String url = this.pageInfoUrl.replaceAll("@id", id).replaceAll("@token", UrlUtil.encodeValue(appToken));
            String jsonResponse = HttpUtil.doGet(url);

            JSONObject jsonObject = new JSONObject(jsonResponse);
            FbProfile fbProfile = new FbProfile();
            fbProfile.setProfile_id(id);
            fbProfile.setProfile_name(JsonUtil.getString(jsonObject, "name"));
            fbProfile.setProfile_username(JsonUtil.getString(jsonObject, "username"));
            fbProfile.setProfile_picture(JsonUtil.getString(jsonObject.getJSONObject("picture").getJSONObject("data"), "url"));
            fbProfile.setProfile_link(JsonUtil.getString(jsonObject, "link"));
            fbProfile.setProfile_category(JsonUtil.getString(jsonObject, "category"));
            fbProfile.setProfile_about(JsonUtil.getString(jsonObject, "about"));
            fbProfile.setFan_count(JsonUtil.getLong(jsonObject, "fan_count"));
            fbProfile.setUpdated_by(GlobalUtil.getSystemUser());
            fbProfile.setUpdated_time(new Date());

            this.fbProfileRepo.save(fbProfile);

            this.redisUtil.addQueue(redisPageKey, fbProfile.getProfile_id(), null, null, "fetch_posts");
        } catch (MyHttpException e) {
            throw new Exception(this.getErrorMessage(e.getMessage()));
        } catch (Exception e) {
            throw e;
        }
    }


    private String getErrorMessage(String message) {
        try{
            return new JSONObject(message).getJSONObject("error").getString("message");
        }catch(Exception e) {
            return message;
        }
    }
}
