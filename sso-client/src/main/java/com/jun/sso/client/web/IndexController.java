package com.jun.sso.client.web;/*
 * Copyright (C), 2017-2018, sunxiaojun
 * FileName: com.octopus.sso.client.web
 * Author:   孙
 * Date:    2018/3/14 11:01
 * Description: //模块目的、功能描述
 */

import com.jun.sso.client.repository.JedisClient;
import com.jun.sso.sdk.pojo.BaseResponse;
import com.jun.sso.sdk.pojo.User;
import com.jun.sso.sdk.service.SSOService;
import com.jun.sso.sdk.utils.CommonUtils;
import com.jun.sso.sdk.utils.CookieUtils;
import com.jun.sso.sdk.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Controller
@PropertySource(value = "classpath:redis.properties")
public class IndexController {
    @Autowired
    private JedisClient jedisClient;

    @Value("${REDIS_USER_SESSION_KEY}")
    private String REDIS_USER_SESSION_KEY;

    @Value("${COOKIE_DOMAIN}")
    private String COOKIE_DOMAIN;

    /**
     * SSO登录地址
     */
    @Value("${SSO_LOGIN_URL}")
    private String SSO_LOGIN_URL;

    /**
     * SSO验证地址
     */
    @Value("${SSO_VALIDATE_URL}")
    private String SSO_VALIDATE_URL;

    /**
     * SSO登出地址
     */
    @Value("${SSO_LOGOUT_URL}")
    private String SSO_LOGOUT_URL;

    /**
     * 当前系统名称
     */
    @Value("${spring.application.name}")
    private String APP_CODE;

    @Autowired
    private SSOService ssoService;

    @RequestMapping(value = "/redirect")
    public String index(@RequestParam(required = false) String token,
                        @RequestParam String redirect_url,
                        HttpServletRequest request,
                        HttpServletResponse response) {
        //token为空时，重定向SSO登录页面
        if (CommonUtils.isNullOrEmpty(token)) {

            redirect_url = String.valueOf(request.getRequestURL());
            return "redirect:" + SSO_LOGIN_URL + APP_CODE + "?redirect_url=" + redirect_url;

        } else {
            //调用SSO-SDK方法，验证token有效性
            BaseResponse baseResponse = ssoService.validate(APP_CODE, token, SSO_VALIDATE_URL);
            //取出token超时时间
            Integer timeout = CommonUtils.getDiff(baseResponse.getTimeout());

            //取出用户信息
            User user = JsonUtils.jsonToPojo(baseResponse.getData().toString(),User.class);

            // 生成业务系统Cookie，存Redis
            String cookie = UUID.randomUUID().toString();
            jedisClient.set(REDIS_USER_SESSION_KEY + ":" + cookie, baseResponse.getData().toString());
            jedisClient.expire(REDIS_USER_SESSION_KEY + ":" + cookie, timeout);
            CookieUtils.setCookie(request, response, COOKIE_DOMAIN, cookie);

            //重定向到业务系统
            return "redirect:" + redirect_url;
        }
    }

    @RequestMapping(value = "/main")
    public String main() {
        return "index";
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public void logout(@RequestParam(required = false) String redirect_url, HttpServletRequest request, HttpServletResponse response) {
        //取出cookie
        String cookie = CookieUtils.getCookieValue(request, COOKIE_DOMAIN);
        if (!CommonUtils.isNullOrEmpty(cookie)) {
            // 删除redis中的用户信息
            jedisClient.del(REDIS_USER_SESSION_KEY + ":" + cookie);
            // 删除cookie
            CookieUtils.deleteCookie(request, response, COOKIE_DOMAIN);
        }

        //重定向到SSO-SERVER登出，
        try {
            ssoService.redirect(APP_CODE,SSO_LOGOUT_URL,redirect_url,request,response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
