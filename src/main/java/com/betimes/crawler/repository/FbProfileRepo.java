package com.betimes.crawler.repository;

import com.betimes.crawler.model.FbProfile;
import org.springframework.data.jpa.repository.JpaRepository;

//@Component
public interface FbProfileRepo extends JpaRepository<FbProfile, String> {
//    @Value("${config.es.search.id}")
//    private String ES_SEARCH_BY_ID_URL;
//
//    private String INDEX = "bts_facebook_profile";
//    private String DOC_TYPE = "json";
//
//    public void save(FbProfile fbProfile) {
//        String url = this.ES_SEARCH_BY_ID_URL.replaceAll("@index", this.INDEX)
//                .replaceAll("@type", this.DOC_TYPE).replaceAll("@id", fbProfile.getId());
//        try {
//            JSONObject jsonBody = new JSONObject(fbProfile);
//            HttpUtil.doPost(url, jsonBody.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
