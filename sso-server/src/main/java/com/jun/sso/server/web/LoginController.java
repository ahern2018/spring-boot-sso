package com.jun.sso.server.web;/*
 * Copyright (C), 2017-2018, sunxiaojun
 * FileName: com.octopus.sso.web
 * Author:   孙
 * Date:    2018/3/8 15:21
 * Description: 登录和注销控制器
 */

import com.jun.sso.server.service.SystemInfoService;
import com.jun.sso.server.service.UserService;
import com.jun.sso.server.pojo.BaseResponse;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private SystemInfoService systemInfoService;

    /**
     * 用户登录
     *
     * @param username     用户名
     * @param password     密码
     * @param appCode      系统编号
     * @param redirect_url 重定向地址
     * @param request      HttpRequest
     * @param response     HttpResponse
     */
    @ApiOperation(value = "用户登录", notes = "根据用户名、密码进行登录", httpMethod = "POST")
    @ApiImplicitParams({@ApiImplicitParam(name = "username", value = "用户名", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "password", value = "密码", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "appCode", value = "系统编号", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "redirect_url", value = "重定向地址", required = true, dataType = "String", paramType = "query")})
    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        @RequestParam String redirect_url,
                        @RequestParam String appCode,
                        HttpServletRequest request,
                        HttpServletResponse response,
                        Model model) {

        logger.info("SSO用户登录,请求参数:[username:{}],[password:{}],[redirect_url:{}],[appCode:{}]", username, password, redirect_url, appCode);
        /**
         * TODO:
         * 1、取用户名、密码进行校验
         * 2、生成token信息
         * 3、生成保存cookie信息
         * 4、保存token到redis
         * 5、重定向到业务系统url --> 增加token信息
         */
        BaseResponse baseResponse = null;
        try {
            baseResponse = userService.userLogin(username, password, appCode, request, response);
            if (baseResponse.getStatus() != 200) {
                logger.info("SSO用户登录失败,[BaseResponse:{}]", baseResponse);
                return "login";
            }
        } catch (Exception e) {
            logger.error("SSO用户登录异常,{}", e);
            return "login";
        }
        String token = baseResponse.getData().toString();

        //SSO 已登录
        baseResponse = systemInfoService.findRedirectUrl(appCode);
        if (baseResponse.getStatus() != 200) {
            logger.info("SSO用户登录成功，取重定向地址失败:[BaseResponse:{}]", baseResponse);
            return "login";
        }

        logger.info("SSO用户登录成功，重定向到业务系统:[BaseResponse:{}]", baseResponse);
        return "redirect:" + baseResponse.getData().toString() + "?token=" + token + "&redirect_url=" + redirect_url;

    }

}
