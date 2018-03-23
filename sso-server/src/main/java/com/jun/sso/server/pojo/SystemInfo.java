package com.jun.sso.server.pojo;/*
 * Copyright (C), 2017-2018, sunxiaojun
 * FileName: com.octopus.sso.server.pojo
 * Author:   孙
 * Date:    2018/3/16 9:31
 * Description: //模块目的、功能描述
 */

import com.jun.sso.server.utils.CommonUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Table(name = "t_sso_system_info")
@Entity
public class SystemInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;                        //自增长主键

    @Column(length = 60)
    @NotNull
    private String appCode;                 //系统编号

    @Column(length = 120)
    private String appName;                 //系统名称

    @NotNull
    private String logoutUrl;               //登出地址

    @NotNull
    private String redirectUrl;             //重定向地址

    @Column(length = 60)
    private String adder;                   //创建人

    @Column(length = 20)
    @NotNull
    private String addDate;                 //创建时间

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getLogoutUrl() {
        return logoutUrl;
    }

    public void setLogoutUrl(String logoutUrl) {
        this.logoutUrl = logoutUrl;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String getAdder() {
        return adder;
    }

    public void setAdder(String adder) {
        this.adder = adder;
    }

    public String getAddDate() {
        return addDate;
    }

    public void setAddDate(String addDate) {
        this.addDate = addDate;
    }

    @Override
    public String toString() {
        return "SystemInfo{" +
                "id=" + id +
                ", appCode='" + appCode + '\'' +
                ", appName='" + appName + '\'' +
                ", logoutUrl='" + logoutUrl + '\'' +
                ", redirectUrl='" + redirectUrl + '\'' +
                ", adder='" + adder + '\'' +
                ", addDate='" + addDate + '\'' +
                '}';
    }

    public SystemInfo() {
    }

    public SystemInfo(@NotNull String appCode, String appName, @NotNull String logoutUrl, @NotNull String redirectUrl, String adder) {
        this.appCode = appCode;
        this.appName = appName;
        this.logoutUrl = logoutUrl;
        this.redirectUrl = redirectUrl;
        this.adder = adder;
        this.addDate = CommonUtils.getCurrentDateTime();
    }
}
