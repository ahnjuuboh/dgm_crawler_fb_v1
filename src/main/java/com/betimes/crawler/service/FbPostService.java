package com.betimes.crawler.service;

import com.betimes.crawler.exception.MyHttpException;
import com.betimes.crawler.model.FacebookContent;
import com.betimes.crawler.model.FbFrom;
import com.betimes.crawler.repository.FacebookContentRepo;
import com.betimes.crawler.util.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class FbPostService {
    private static final Logger log = LoggerFactory.getLogger(FbPostService.class);

    @Autowired
    private FacebookContentRepo facebookContentRepo;
    @Autowired
    private RedisUtil redisUtil;

    @Value("${app.token}")
    private String appToken;
    @Value("${config.fb.page.post.url}")
    private String pagePostUrl;
    @Value("${config.fb.page.post.fields}")
    private String pagePostFields;
    @Value("${config.redis.facebook.comment.key}")
    private String redisCommentKey;

    public void fetchPost(String id, String url) throws Exception {
        log.info(id + "|fetchPost|Start");
        List<FacebookContent> contentList = new ArrayList<>();

        try {
            if (url == null) {
                url = this.pagePostUrl.replaceAll("@id", id)
                        .replaceAll("@fields", UrlUtil.encodeValue(this.pagePostFields))
                        .replaceAll("@token", UrlUtil.encodeValue(this.appToken));
            }
            String jsonResponse = HttpUtil.doGet(url);

            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray dataList = jsonObject.getJSONArray("data");
            int size = dataList.length();
            for (int i = 0; i < size; i++) {
                JSONObject dataJson = dataList.getJSONObject(i);
                String postId = JsonUtil.getString(dataJson, "id");
                FacebookContent content = new FacebookContent();
                content.setId(postId);
                content.setType(GlobalUtil.getPostType());
                content.setSource_type(GlobalUtil.getFacebookSource());
                content.setSource_id(id);

                FbFrom from = new FbFrom();
                from.setId(JsonUtil.getString(dataJson.getJSONObject("from"), "id"));
                from.setName(JsonUtil.getString(dataJson.getJSONObject("from"), "name"));
                content.setFrom(from);

                content.setMessage(JsonUtil.getString(dataJson, "message"));
                content.setStatus_type(JsonUtil.getString(dataJson, "status_type"));
                content.setPermalink_url(JsonUtil.getString(dataJson, "permalink_url"));
                content.setPicture(JsonUtil.getString(dataJson, "full_picture"));

                if (!dataJson.isNull("attachments")) {
                    content.setMedia_type(JsonUtil.getString(dataJson.getJSONObject("attachments").getJSONArray("data")
                            .getJSONObject(0), "media_type"));
                    content.setLink(JsonUtil.getString(dataJson.getJSONObject("attachments").getJSONArray("data")
                            .getJSONObject(0), "url"));
                }

                content.setLikes(JsonUtil.getLong(dataJson.getJSONObject("reactions_like").getJSONObject("summary"), "total_count"));
                content.setLove(JsonUtil.getLong(dataJson.getJSONObject("reactions_love").getJSONObject("summary"), "total_count"));
                content.setHaha(JsonUtil.getLong(dataJson.getJSONObject("reactions_haha").getJSONObject("summary"), "total_count"));
                content.setWow(JsonUtil.getLong(dataJson.getJSONObject("reactions_wow").getJSONObject("summary"), "total_count"));
                content.setSorry(JsonUtil.getLong(dataJson.getJSONObject("reactions_sad").getJSONObject("summary"), "total_count"));
                content.setAnger(JsonUtil.getLong(dataJson.getJSONObject("reactions_angry").getJSONObject("summary"), "total_count"));
                content.setDiskiles(0L);
                content.setView(0L);
                content.setShares(0L);
                if (!dataJson.isNull("shares")) {
                    content.setShares(JsonUtil.getLong(dataJson.getJSONObject("shares"), "count"));
                }
                content.setComments(JsonUtil.getLong(dataJson.getJSONObject("comments").getJSONObject("summary"), "total_count"));
                content.setCreated_time(JsonUtil.getString(dataJson, "created_time"));

                contentList.add(content);
            }
            for (FacebookContent c : contentList) {
                this.facebookContentRepo.save(c);
                if (c.getComments() > 0) this.redisUtil.addQueue(redisCommentKey, c.getId(), id, null, "fetch_comments");
            }
            log.info(id + "|post list size:" + contentList.size());
//            if (!jsonObject.isNull("paging")) {
//                if (!jsonObject.getJSONObject("paging").isNull("next")) {
//                    String nextPageUrl = JsonUtil.getString(jsonObject.getJSONObject("paging"), "next");
//                    this.redisUtil.addQueue(id, nextPageUrl, "fetch_posts");
//                }
//            }
        } catch (MyHttpException e) {
            throw new Exception(this.getErrorMessage(e.getMessage()));
        } catch (Exception e) {
            throw e;
        }
    }

    private String getErrorMessage(String message) {
        try {
            return new JSONObject(message).getJSONObject("error").getString("message");
        } catch (Exception e) {
            return message;
        }
    }
}
