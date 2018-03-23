package com.jun.sso.client.filter;/*
 * Copyright (C), 2017-2018, sunxiaojun
 * FileName: com.octopus.sso.client.filter
 * Author:   孙
 * Date:    2018/3/16 13:40
 * Description: 自定义拦截器
 */

import com.jun.sso.client.repository.JedisClient;
import com.jun.sso.sdk.service.SSOService;
import com.jun.sso.sdk.utils.CommonUtils;
import com.jun.sso.sdk.utils.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@PropertySource(value = "classpath:redis.properties")
public class SessionInterceptor implements HandlerInterceptor {

    @Autowired
    private JedisClient jedisClient;

    @Value("${REDIS_USER_SESSION_KEY}")
    private String REDIS_USER_SESSION_KEY;

    @Value("${COOKIE_DOMAIN}")
    private String COOKIE_DOMAIN;

    @Value("${SSO_LOGIN_URL}")
    private String SSO_LOGIN_URL;

    @Value("${spring.application.name}")
    private String APP_CODE;

    @Autowired
    private SSOService ssoService;

    // controller 执行之前调用
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        //验证Cookie是否存在
        String cookie = CookieUtils.getCookieValue(request, COOKIE_DOMAIN);
        String redirect_url = String.valueOf(request.getRequestURL());
        if (CommonUtils.isNullOrEmpty(cookie)) {    //Cookie不存在跳转到单点登录系统
            try {
                ssoService.redirect(APP_CODE, SSO_LOGIN_URL, redirect_url, request, response);
            } catch (IOException e) {
                return false;
            }
            return false;
        }
        //验证Redis中是否存在cookie信息
        String result = jedisClient.get(REDIS_USER_SESSION_KEY + ":" + cookie);
        if (CommonUtils.isNullOrEmpty(result)) {
            try {
                ssoService.redirect(APP_CODE, SSO_LOGIN_URL, redirect_url, request, response);
            } catch (IOException e) {

                return false;
            }
            return false;
        }
        return true;
    }
}
