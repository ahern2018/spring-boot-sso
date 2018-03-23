package com.jun.sso.server.web;
/*
 * Copyright (C), 2017-2018, sunxiaojun
 * FileName: com.octopus.sso.server.web
 * Author:   孙
 * Date:    2018/3/13 14:57
 * Description: 认证中心页面显示控制器
 */

import com.jun.sso.server.service.SystemInfoService;
import com.jun.sso.server.service.UserService;
import com.jun.sso.server.utils.CommonUtils;
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
public class IndexController {

    //记录日志
    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private SystemInfoService systemInfoService;

    /**
     * SSO单点登录-用户登录接口
     *
     * @param appCode      系统编号
     * @param redirect_url 重定向地址
     * @param request      HttpRequest
     */
    @ApiOperation(value = "业务系统重定向到SSO用户登录", notes = "业务系统重定向到SSO单点登录系统，进行用户验证", httpMethod = "GET")
    @ApiImplicitParams({@ApiImplicitParam(name = "appCode", value = "系统编号", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "redirect_url", value = "重定向地址", required = true, dataType = "String", paramType = "query")})
    @RequestMapping(value = "/sso/login/{appCode}", method = RequestMethod.GET)
    public String login(@PathVariable(value = "appCode") String appCode,
                        @RequestParam String redirect_url,
                        HttpServletRequest request,
                        Model model) {
        logger.info("业务系统重定向到SSO用户登录,请求参数:[appCode：{}],[redirect_url:{}]", appCode, redirect_url);
        /**
         * TODO:
         *  1、取Cookie
         *  2、若Cookie不存在，则跳转登录页面
         *  3、若Cookie存在，则校验Cookie是否合法，Cookie不合法(跳转登录页面)；Cookie合法（生成Token，拼接到redirect_url后，返回redirect_url）
         */
        BaseResponse baseResponse = userService.validateCookie(appCode, request);
        // SSO未登录
        if (baseResponse.getStatus() != 200) {
            logger.info("SSO用户未登录：[BaseResponse:{}],重定向到SSO登录页面。", baseResponse);
            model.addAttribute("redirect_url", redirect_url);
            model.addAttribute("appCode", appCode);
            return "login";
        }
        String token = baseResponse.getData().toString();

        //SSO 已登录
        baseResponse = systemInfoService.findRedirectUrl(appCode);
        if (baseResponse.getStatus() != 200) {
            logger.info("SSO用户已登录:[BaseResponse:{}],重定向到SSO登录页面。", baseResponse);
            model.addAttribute("redirect_url", redirect_url);
            model.addAttribute("appCode", appCode);
            model.addAttribute("message", baseResponse.getData().toString());
            return "login";
        }
        logger.info("SSO用户已登录:[BaseResponse:{}],重定向到业务系统验证页面", baseResponse);

        return "redirect:" + baseResponse.getData().toString() + "?token=" + token + "&redirect_url=" + redirect_url;

    }


    /**
     * SSO验证Token的有效性
     *
     * @param appCode  系统编号
     * @param token    登录票据
     * @param request  HttpRequest
     * @param response HttpResponse
     * @return 验证是否通过
     */
    @ApiOperation(value = "SSO验证Token的有效性", notes = "验证业务系统传递过来的Token的有效性", httpMethod = "POST")
    @ApiImplicitParams({@ApiImplicitParam(name = "appCode", value = "系统编号", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "token", value = "登录票据", required = true, dataType = "String", paramType = "path")})
    @ResponseBody
    @RequestMapping(value = "/sso/validate", method = RequestMethod.POST)
    public BaseResponse validate(@RequestParam(value = "appCode") String appCode,
                                 @RequestParam(value = "token") String token,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {
        /**
         * TODO:
         *  1、验证Token的有效性
         *  2、返回用户信息
         */
        logger.info("验证Ticket（票据）有效性,请求参数：[appCode:{}],[Ticket:{}]", appCode, token);
        BaseResponse result = null;
        try {
            result = userService.validateToken(token);
        } catch (Exception e) {
            logger.error("验证Ticket（票据）有效性异常,{}", e);
            result = BaseResponse.build(500, "");
        }
        logger.info("验证Ticket（票据）有效性,返回结果：[result:{}]", result);
        return result;
    }

    /**
     * SSO用户登出
     *
     * @param appCode  系统编号
     * @param request  HttpRequest
     * @param response HttpResponse
     */
    @ApiOperation(value = "SSO用户登出", notes = "登录业务系统,同时登出SSO系统", httpMethod = "GET")
    @ApiImplicitParams({@ApiImplicitParam(name = "appCode", value = "系统编号", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "redirect_url", value = "系统编号", required = false, dataType = "String", paramType = "query")})
    @RequestMapping(value = "/sso/logout/{appCode}", method = RequestMethod.GET)
    public String logout(@PathVariable(value = "appCode") String appCode,
                         @RequestParam(required = false) String redirect_url,
                         HttpServletRequest request,
                         HttpServletResponse response,
                         Model model) {

        logger.info("业务系统登出，请求参数:[appCode:{},redirect_url:{}]", appCode, redirect_url);
        /**
         * TODO:
         *  根据AppCode遍历活跃状态的客户端，调用客户端接口，进行登出操作
         */
        BaseResponse baseResponse = userService.logout(appCode, request, response);

        // 单点登出执行完毕，跳转登录页面
        if (baseResponse.getStatus() != 200) {
            if (!CommonUtils.isNullOrEmpty(redirect_url)) {
                logger.info("单点登出执行完毕，跳转传入到网页:[BaseResponse:{},redirect_url:{}]", baseResponse, redirect_url);
                return "redirect:" + redirect_url;
            }

            logger.info("单点登出执行完毕，跳转SSO登录页面:[BaseResponse:{}]", baseResponse);
            model.addAttribute("message", baseResponse.getMsg());
            return "login";
        }
        //重定向到系统进行登出
        String logoutUrl = baseResponse.getData().toString();
        logger.info("执行业务系统单点登录:[BaseResponse:{}]", baseResponse);
        return "redirect:" + logoutUrl;

    }
}
