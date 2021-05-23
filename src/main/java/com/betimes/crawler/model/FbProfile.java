package com.betimes.crawler.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "bts_fb_profile")
public class FbProfile {
    @Id
    @Column(name = "profile_id")
    private String profile_id;
    @Column(name = "profile_name")
    private String profile_name;
    @Column(name = "profile_username")
    private String profile_username;
    @Column(name = "profile_picture")
    private String profile_picture;
    @Column(name = "profile_link")
    private String profile_link;
    @Column(name = "profile_category")
    private String profile_category;
    @Column(name = "profile_about")
    private String profile_about;
    @Column(name = "fan_count")
    private Long fan_count;
    @Column(name = "updated_by")
    private String updated_by;
    @Column(name = "updated_time")
    private Date updated_time;

    public String getProfile_id() {
        return profile_id;
    }

    public void setProfile_id(String profile_id) {
        this.profile_id = profile_id;
    }

    public String getProfile_name() {
        return profile_name;
    }

    public void setProfile_name(String profile_name) {
        this.profile_name = profile_name;
    }

    public String getProfile_username() {
        return profile_username;
    }

    public void setProfile_username(String profile_username) {
        this.profile_username = profile_username;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }

    public String getProfile_link() {
        return profile_link;
    }

    public void setProfile_link(String profile_link) {
        this.profile_link = profile_link;
    }

    public String getProfile_category() {
        return profile_category;
    }

    public void setProfile_category(String profile_category) {
        this.profile_category = profile_category;
    }

    public String getProfile_about() {
        return profile_about;
    }

    public void setProfile_about(String profile_about) {
        this.profile_about = profile_about;
    }

    public Long getFan_count() {
        return fan_count;
    }

    public void setFan_count(Long fan_count) {
        this.fan_count = fan_count;
    }

    public String getUpdated_by() {
        return updated_by;
    }

    public void setUpdated_by(String updated_by) {
        this.updated_by = updated_by;
    }

    public Date getUpdated_time() {
        return updated_time;
    }

    public void setUpdated_time(Date updated_time) {
        this.updated_time = updated_time;
    }
}
