package com.betimes.crawler;

import com.betimes.crawler.service.FbCommentService;
import com.betimes.crawler.service.FbPostService;
import com.betimes.crawler.service.FbProfileService;
import com.betimes.crawler.util.JsonUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import redis.clients.jedis.Jedis;

import java.util.Date;

@SpringBootApplication
public class Application {
    @Autowired
    private FbProfileService fbProfileService;
    @Autowired
    private FbPostService fbPostService;
    @Autowired
    private FbCommentService fbCommentService;

    @Value("${redis.server}")
    private String REDIS_HOST;
    @Value("${config.redis.facebook.page.key}")
    private String redisPageKey;
    @Value("${config.redis.facebook.comment.key}")
    private String redisCommentKey;

    public static void main(String[] args) {
//        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
//        context.getBean(ServiceStart.class).queueManagement();
//    }
        SpringApplication.run(Application.class, args);
    }

//    @Scheduled(cron = "*/30 * 18-23,0-8 * * ?", zone = "Asia/Bangkok") // cron expression is 'schedule every 30s at 18.00 - 08.00 everyday
    @Scheduled(cron = "*/20 * * * * ?", zone = "Asia/Bangkok")
    void fetchPosts() {
        try {
            Jedis j = new Jedis(REDIS_HOST);
            String messages = j.lpop(this.redisPageKey);
            if (messages != null) {
                JSONObject pageObj = new JSONObject(messages);

                String url = JsonUtil.getString(pageObj, "url");
                switch (pageObj.getString("type")) {
                    case "fetch_profile":
                        this.fbProfileService.fetchInfo(JsonUtil.getString(pageObj, "id"));
                        break;
                    case "fetch_posts":
                        this.fbPostService.fetchPost(JsonUtil.getString(pageObj, "id"), url);
                        break;
                }
            } else {
                messages = j.lpop(this.redisCommentKey);
                if (messages != null) {
                    JSONObject commentObj = new JSONObject(messages);
                    String url = JsonUtil.getString(commentObj, "url");
                    this.fbCommentService.fetchComment(JsonUtil.getString(commentObj, "id"),
                            JsonUtil.getString(commentObj, "source_id"), url);
                }
            }
            j.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

@Configuration
@EnableScheduling
@ConditionalOnProperty(name = "scheduling.enabled", matchIfMissing = true)
class SchedulingConfiguration {
}