package com.jun.sso.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan(basePackages = {"com.jun.sso"})
public class SsoClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(SsoClientApplication.class, args);
	}
}
