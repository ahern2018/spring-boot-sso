package com.jun.sso.server.service;/*
 * Copyright (C), 2017-2018, sunxiaojun
 * FileName: com.octopus.sso.server.service
 * Author:   孙
 * Date:    2018/3/17 9:30
 * Description: //模块目的、功能描述
 */

import com.jun.sso.server.repository.SystemInfoRepository;
import com.jun.sso.server.pojo.BaseResponse;
import com.jun.sso.server.pojo.SystemInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SystemInfoService {

    @Autowired
    private SystemInfoRepository systemInfoRepository;

    public BaseResponse register(SystemInfo systemInfo) {
        // 检查AppCode是否注册，一般在前端验证的时候处理，因为注册不存在高并发的情况，这里再加一层查询是不影响性能的
        if (null != systemInfoRepository.findByAppCode(systemInfo.getAppCode())) {
            return BaseResponse.build(400, "当前系统编号已存在");
        }
        systemInfoRepository.save(systemInfo);

        return BaseResponse.build(200, "登记成功");
    }

    /**
     * 根据AppCode 查询重定向的Url
     *
     * @param appCode 系统代号
     * @return
     */
    public BaseResponse findRedirectUrl(String appCode) {
        SystemInfo systemInfo = systemInfoRepository.findByAppCode(appCode);
        if (null == systemInfo) {
            return BaseResponse.build(400, "系统未注册");
        }
        return BaseResponse.ok(systemInfo.getRedirectUrl().toString());
    }

    public Iterable<SystemInfo> findAll() {
        return systemInfoRepository.findAll();
    }
}
