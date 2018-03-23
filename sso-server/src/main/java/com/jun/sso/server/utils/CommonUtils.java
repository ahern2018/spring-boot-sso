package com.jun.sso.server.utils;/*
 * Copyright (C), 2017-2018, sunxiaojun
 * FileName: com.octopus.sso.server.utils
 * Author:   孙
 * Date:    2018/3/13 14:38
 * Description: //模块目的、功能描述
 */

import com.jun.sso.server.pojo.User;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

public class CommonUtils {
    /**
     * 加盐加密的策略非常多,根据实际业务来
     */
    public static void entryptPassword(User user) {
        String salt = UUID.randomUUID().toString();
        String temPassword = salt + user.getPlainPassword();
        String md5Password = DigestUtils.md5DigestAsHex(temPassword.getBytes());
        user.setSalt(salt);
        user.setPassword(md5Password);
    }

    public static boolean decryptPassword(User user, String plainPassword) {
        String temPassword = user.getSalt() + plainPassword;
        String md5Password = DigestUtils.md5DigestAsHex(temPassword.getBytes());
        return user.getPassword().equals(md5Password);
    }

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

    public static boolean isNullOrEmpty(Object object) {
        return object == null || "".equals(object) || !"null".equals(object);
    }

    public static String getIPAddress(HttpServletRequest request) {
        String ip = null;

        //X-Forwarded-For：Squid 服务代理
        String ipAddresses = request.getHeader("X-Forwarded-For");

        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            //Proxy-Client-IP：apache 服务代理
            ipAddresses = request.getHeader("Proxy-Client-IP");
        }

        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            //WL-Proxy-Client-IP：weblogic 服务代理
            ipAddresses = request.getHeader("WL-Proxy-Client-IP");
        }

        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            //HTTP_CLIENT_IP：有些代理服务器
            ipAddresses = request.getHeader("HTTP_CLIENT_IP");
        }

        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            //X-Real-IP：nginx服务代理
            ipAddresses = request.getHeader("X-Real-IP");
        }

        //有些网络通过多层代理，那么获取到的ip就会有多个，一般都是通过逗号（,）分割开来，并且第一个ip为客户端的真实IP
        if (ipAddresses != null && ipAddresses.length() != 0) {
            ip = ipAddresses.split(",")[0];
        }

        //还是不能获取到，最后再通过request.getRemoteAddr();获取
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
