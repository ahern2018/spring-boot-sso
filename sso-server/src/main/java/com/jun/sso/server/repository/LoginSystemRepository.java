package com.jun.sso.server.repository;/*
 * Copyright (C), 2017-2018, sunxiaojun
 * FileName: com.octopus.sso.server.repository
 * Author:   孙
 * Date:    2018/3/20 10:07
 * Description: //模块目的、功能描述
 */

import com.jun.sso.server.pojo.LoginSystem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LoginSystemRepository extends PagingAndSortingRepository<LoginSystem, Long>,
        JpaSpecificationExecutor<LoginSystem> {

    /**
     * 根据appCode和cookie更新状态
     *
     * @param status  状态
     * @param appCode 系统编号
     * @param cookie  Cookie
     * @return 修改记录数
     */
    @Modifying
    @Query(value = "UPDATE LoginSystem a SET a.status = :status,a.logoutDate = :logoutDate WHERE a.appCode = :appCode AND a.cookie = :cookie")
    public Integer updateStatusByAppCodeAndAndCookie(@Param("logoutDate") String logoutDate, @Param("status") Integer status, @Param("appCode") String appCode, @Param("cookie") String cookie);


    @Query(value = "SELECT DISTINCT b.logoutUrl FROM LoginSystem a LEFT JOIN com.jun.sso.server.pojo.SystemInfo b ON a.appCode = b.appCode " +
            "WHERE a.cookie = :cookie AND a.status = :status")
    public List<String> findLogoutUrlByCookieAndStatus(@Param("status") Integer status, @Param("cookie") String cookie, Pageable pageable);
}
