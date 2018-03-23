package com.jun.sso.server.enums;/*
 * Copyright (C), 2017-2018, sunxiaojun
 * FileName: com.octopus.sso.server.enums
 * Author:   孙
 * Date:    2018/3/20 10:58
 * Description: //模块目的、功能描述
 */

public enum LoginStatusEnum {

    LOGINING("已登录", 1),
    LOGOUT("已登出", 0);

    private String name;
    private int index;

    private LoginStatusEnum(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * 根据index查询name名称
     *
     * @param index
     * @return
     */
    public static String getName(int index) {
        for (LoginStatusEnum c : LoginStatusEnum.values()) {
            if (c.getIndex() == index) {
                return c.name;
            }
        }
        return null;
    }
}
