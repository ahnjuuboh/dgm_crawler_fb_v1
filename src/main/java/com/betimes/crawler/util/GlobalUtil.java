package com.betimes.crawler.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GlobalUtil {
    public static String getCurrentDateTime() {
        Date date = new Date();

        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(date);
    }

    public static String getFacebookSource() {
        return "facebook";
    }

    public static String getCommentType() {
        return "comment";
    }

    public static String getPostType() {
        return "post";
    }

    public static String getActiveStatus() {
        return "A";
    }

    public static String getSystemUser() {
        return "SYSTEM";
    }
}
