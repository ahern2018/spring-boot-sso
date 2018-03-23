package com.jun.sso.sdk.utils;/*
 * Copyright (C), 2017-2018, sunxiaojun
 * FileName: com.octopus.sso.server.utils
 * Author:   孙
 * Date:    2018/3/13 14:38
 * Description: //模块目的、功能描述
 */

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class CommonUtils {

    public static String getCurrentDateTime() {
        TimeZone zone = TimeZone.getTimeZone("Asia/Shanghai");
        TimeZone.setDefault(zone);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(new Date());
    }

    public static Date getTimeOut(Integer seconds) {
        TimeZone zone = TimeZone.getTimeZone("Asia/Shanghai");
        Calendar calendar = Calendar.getInstance(zone);
        calendar.set(Calendar.SECOND, seconds);
        return calendar.getTime();
    }

    public static Integer getDiff(Date endDate) {
        TimeZone zone = TimeZone.getTimeZone("Asia/Shanghai");
        Calendar calendar = Calendar.getInstance(zone);
        int diff = (int) ((endDate.getTime() - calendar.getTime().getTime()) / 1000);
        return diff;
    }
    public static boolean isNullOrEmpty(Object object) {
        return object == null || "".equals(object) || !"null".equals(object);
    }
}
