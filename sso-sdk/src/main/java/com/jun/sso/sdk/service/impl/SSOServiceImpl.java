package com.jun.sso.sdk.service.impl;/*
 * Copyright (C), 2017-2018, sunxiaojun
 * FileName: com.octopus.sso.sdk.service.impl
 * Author:   孙
 * Date:    2018/3/23 10:02
 * Description: //模块目的、功能描述
 */

import com.jun.sso.sdk.service.SSOService;
import com.jun.sso.sdk.pojo.BaseResponse;
import com.jun.sso.sdk.utils.HttpClientUtil;
import com.jun.sso.sdk.utils.JsonUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class SSOServiceImpl implements SSOService {

    @Override
    public void redirect(String appCode, String redirectUrl, String localUrl, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String redirect_url = redirectUrl + appCode + "?redirect_url=" + localUrl;
        response.sendRedirect(redirect_url);

    }

    @Override
    public BaseResponse validate(String appCode, String token, String validateUrl) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("token", token);
        map.put("appCode", appCode);
        String result = HttpClientUtil.doPost(validateUrl, map);

        //解析result,取出超时时间
        return JsonUtils.jsonToPojo(result, BaseResponse.class);
    }

}
