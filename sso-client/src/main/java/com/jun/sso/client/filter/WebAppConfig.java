package com.jun.sso.client.filter;/*
 * Copyright (C), 2017-2018, sunxiaojun
 * FileName: com.octopus.sso.client.filter
 * Author:   孙
 * Date:    2018/3/17 9:12
 * Description: 注册自定义拦截器
 */

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebAppConfig extends WebMvcConfigurerAdapter {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionInterceptor()).excludePathPatterns("/redirect","/logout");
    }

    //对拦截器进行Bean处理
    @Bean
    public SessionInterceptor sessionInterceptor(){
        return new SessionInterceptor();
    }
}
