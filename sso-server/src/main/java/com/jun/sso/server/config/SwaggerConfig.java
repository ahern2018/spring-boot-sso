package com.jun.sso.server.config;/*
 * Copyright (C), 2017-2018, sunxiaojun
 * FileName: com.octopus.sso.config
 * Author:   孙
 * Date:    2018/3/8 15:17
 * Description: // Swagger配置类
 */

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfig {

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("springboot利用swagger构建api文档")
//                .description("简单优雅的restfun风格，http://blog.csdn.net/saytime")
//                .termsOfServiceUrl("http://blog.csdn.net/saytime")
                .version("1.0")
                .build();
    }

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.octopus.sso"))
                .paths(PathSelectors.any())
                .build();
    }

}
