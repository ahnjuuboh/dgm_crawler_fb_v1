package com.betimes.crawler.service;

import com.betimes.crawler.exception.MyHttpException;
import com.betimes.crawler.model.FacebookContent;
import com.betimes.crawler.model.FbCommentParent;
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
public class FbCommentService {
    private static final Logger log = LoggerFactory.getLogger(FbCommentService.class);

    @Autowired
    private FacebookContentRepo facebookContentRepo;
    @Autowired
    private RedisUtil redisUtil;

    @Value("${app.token}")
    private String appToken;
    @Value("${config.fb.post.comment.url}")
    private String postCommentUrl;
    @Value("${config.fb.post.comment.fields}")
    private String postCommentFields;
    @Value("${config.redis.facebook.comment.key}")
    private String redisCommentKey;

    public void fetchComment(String id, String sourceId, String url) throws Exception {
        log.info(id + "|fetchComment|Start");
        List<FacebookContent> contentList = new ArrayList<>();

        try {
            if (url == null) {
                url = this.postCommentUrl.replaceAll("@id", id)
                        .replaceAll("@fields", UrlUtil.encodeValue(this.postCommentFields))
                        .replaceAll("@token", UrlUtil.encodeValue(this.appToken));
            }
            String jsonResponse = HttpUtil.doGet(url);
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray dataList = jsonObject.getJSONArray("data");
            int size = dataList.length();
            for (int i = 0; i < size; i++) {
                JSONObject dataJson = dataList.getJSONObject(i);
                String commentId = JsonUtil.getString(dataJson, "id");
                FacebookContent content = new FacebookContent();
                content.setId(commentId);
                content.setType(GlobalUtil.getCommentType());
                content.setSource_type(GlobalUtil.getFacebookSource());
                content.setSource_id(sourceId);

                if (!dataJson.isNull("parent")) {
                    FbCommentParent parent = new FbCommentParent();
                    parent.setId(JsonUtil.getString(dataJson.getJSONObject("parent"), "id"));
                    parent.setMessage(JsonUtil.getString(dataJson.getJSONObject("parent"), "message"));
                    parent.setCreated_time(JsonUtil.getString(dataJson.getJSONObject("parent"), "created_time"));
                    content.setParent(parent);
                } else {
                    FbFrom from = new FbFrom();
                    from.setId(id);
                    content.setFrom(from);
                }

                content.setMessage(JsonUtil.getString(dataJson, "message"));

                if (!dataJson.isNull("attachment")) {
                    content.setMedia_type(JsonUtil.getString(dataJson.getJSONObject("attachment"), "type"));
                    content.setLink(JsonUtil.getString(dataJson.getJSONObject("attachment"), "url"));
                    if (!dataJson.getJSONObject("attachment").isNull("media")) {
                        content.setPicture(JsonUtil.getString(dataJson.getJSONObject("attachment").getJSONObject("media")
                                .getJSONObject("image"), "src"));
                    }
                }

                content.setLikes(JsonUtil.getLong(dataJson.getJSONObject("reactions_like").getJSONObject("summary"), "total_count"));
                content.setLove(JsonUtil.getLong(dataJson.getJSONObject("reactions_love").getJSONObject("summary"), "total_count"));
                content.setHaha(JsonUtil.getLong(dataJson.getJSONObject("reactions_haha").getJSONObject("summary"), "total_count"));
                content.setWow(JsonUtil.getLong(dataJson.getJSONObject("reactions_wow").getJSONObject("summary"), "total_count"));
                content.setSorry(JsonUtil.getLong(dataJson.getJSONObject("reactions_sad").getJSONObject("summary"), "total_count"));
                content.setAnger(JsonUtil.getLong(dataJson.getJSONObject("reactions_angry").getJSONObject("summary"), "total_count"));
                content.setShares(0L);
                content.setDiskiles(0L);
                content.setView(0L);
                content.setComments(JsonUtil.getLong(dataJson, "comment_count"));
                content.setCreated_time(JsonUtil.getString(dataJson, "created_time"));

                contentList.add(content);
            }

            for (FacebookContent c : contentList) {
                this.facebookContentRepo.save(c);
                if (c.getComments() > 0) this.redisUtil.addQueue(redisCommentKey, c.getId(), sourceId, null, "fetch_comments");
            }
            log.info(id + "|comment list size:" + contentList.size());

//            if (!jsonObject.isNull("paging")) {
//                if (!jsonObject.getJSONObject("paging").isNull("next")) {
//                    String nextPageUrl = JsonUtil.getString(jsonObject.getJSONObject("paging"), "next");
//                    this.redisUtil.addQueue(redisCommentKey, id, sourceId, nextPageUrl, "fetch_comments");
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