package com.jun.sso.server.web;/*
 * Copyright (C), 2017-2018, sunxiaojun
 * FileName: com.octopus.sso.server.web
 * Author:   孙
 * Date:    2018/3/22 10:56
 * Description: 系统注册，查询
 */

import com.jun.sso.server.service.SystemInfoService;
import com.jun.sso.server.utils.CommonUtils;
import com.jun.sso.server.pojo.BaseResponse;
import com.jun.sso.server.pojo.SystemInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = "/system")
public class SystemController {

    private static final Logger logger = LoggerFactory.getLogger(SystemController.class);

    @Autowired
    private SystemInfoService systemInfoService;

    /**
     * 打开系统注册页面
     *
     * @return
     */
    @RequestMapping(value = "/register.htm")
    public String info(Model model) {
        //打开系统注册页面

        model.addAttribute("info", new SystemInfo());
        model.addAttribute("message", "");
        return "/system/register";
    }

    /**
     * 系统注册操作
     *
     * @param appCode     系统编号
     * @param appName     系统名称
     * @param logoutUrl   登出地址
     * @param redirectUrl 授权地址
     * @param adder       添加人
     * @param request     Http请求
     * @param model       Model
     * @return 成功页面-success 注册页面-register
     */
    @RequestMapping(value = "/register.do")
    public String register(@RequestParam String appCode,
                           @RequestParam String appName,
                           @RequestParam String logoutUrl,
                           @RequestParam String redirectUrl,
                           @RequestParam String adder,
                           HttpServletRequest request,
                           Model model) {

        SystemInfo systemInfo = new SystemInfo(appCode, appName, logoutUrl, redirectUrl, adder);
        logger.info("注册系统到单点登录服务,发起方IP为：{}", CommonUtils.getIPAddress(request));
        logger.info("系统注册，请求参数:[SystemInfo:{}]", systemInfo);

        /** 系统注册 **/
        BaseResponse baseResponse = systemInfoService.register(systemInfo);

        /** 判断注册结果 **/
        if (baseResponse.getStatus() == 400) {
            model.addAttribute("message", baseResponse.getMsg().toString());
            model.addAttribute("info", systemInfo);
        } else {
            model.addAttribute("message", "注册成功");
            model.addAttribute("info", new SystemInfo());
        }
        logger.info("系统注册结束,返回结果:[BaseResponse:{}]", baseResponse);

        return "/system/register";
    }
}
