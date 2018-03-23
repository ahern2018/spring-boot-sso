package com.jun.sso.sdk.service;/*
 * Copyright (C), 2017-2018, sunxiaojun
 * FileName: com.octopus.sso.sdk.service
 * Author:   孙
 * Date:    2018/3/23 10:02
 * Description: //模块目的、功能描述
 */

import com.jun.sso.sdk.pojo.BaseResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface SSOService {

    /**
     * 重定向到登录、登出
     *
     * @param appCode     系统编号
     * @param redirectUrl 重定向地址
     * @param localUrl    本地访问地址
     * @param request     HTTP请求
     * @param response    HTTP返回
     */
    public void redirect(String appCode, String redirectUrl, String localUrl, HttpServletRequest request, HttpServletResponse response) throws IOException;

    /**
     * 验证token是否合法
     *
     * @param appCode       系统编号
     * @param token         票据号
     * @param validateUrl   验证地址
     *
     * @return BaseResponse
     */
    public BaseResponse validate(String appCode, String token, String validateUrl);

}
