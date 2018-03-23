package com.jun.sso.server.repository;/*
 * Copyright (C), 2017-2018, sunxiaojun
 * FileName: com.octopus.sso.server.repository
 * Author:   孙
 * Date:    2018/3/17 9:31
 * Description: //模块目的、功能描述
 */

import com.jun.sso.server.pojo.SystemInfo;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface SystemInfoRepository extends PagingAndSortingRepository<SystemInfo, Long>,
        JpaSpecificationExecutor<SystemInfo> {

    // 通过AppCode 查找系统信息
    SystemInfo findByAppCode(String appCode);

}
