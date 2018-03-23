package com.jun.sso.server.pojo;/*
 * Copyright (C), 2017-2018, sunxiaojun
 * FileName: com.octopus.sso.server.pojo
 * Author:   孙
 * Date:    2018/3/20 9:52
 * Description: //模块目的、功能描述
 */

import com.jun.sso.server.utils.CommonUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Table(name = "t_sso_login_system")
@Entity
public class LoginSystem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;                        //自增长主键

    @Column(length = 60)
    @NotNull
    private String appCode;                 //系统编号

    @Column(length = 60)
    @NotNull
    private String cookie;                  //Cookie

    @Column(length = 1)
    @NotNull
    private int status;                     //状态

    @Column(length = 20)
    @NotNull
    private String loginDate;                //登录时间

    @Column(length = 20)
    private String logoutDate;               //登出时间

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

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(String loginDate) {
        this.loginDate = loginDate;
    }

    public String getLogoutDate() {
        return logoutDate;
    }

    public void setLogoutDate(String logoutDate) {
        this.logoutDate = logoutDate;
    }

    public LoginSystem() {
    }

    public LoginSystem(@NotNull String appCode, @NotNull String cookie, @NotNull int status) {
        this.appCode = appCode;
        this.cookie = cookie;
        this.status = status;
        this.loginDate = CommonUtils.getCurrentDateTime();
    }

    @Override
    public String toString() {
        return "LoginSystem{" +
                "id=" + id +
                ", appCode='" + appCode + '\'' +
                ", cookie='" + cookie + '\'' +
                ", status=" + status +
                ", loginDate='" + loginDate + '\'' +
                ", logoutDate='" + logoutDate + '\'' +
                '}';
    }
}
