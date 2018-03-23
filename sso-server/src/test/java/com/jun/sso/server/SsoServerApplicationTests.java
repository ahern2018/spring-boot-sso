package com.jun.sso.server;

import com.jun.sso.server.service.UserService;
import com.jun.sso.server.utils.CommonUtils;
import com.jun.sso.server.pojo.SystemInfo;
import com.jun.sso.server.pojo.User;
import com.jun.sso.server.service.SystemInfoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=SsoServerApplication.class)
public class SsoServerApplicationTests {

    @Autowired
    private UserService userService;

	@Test	// 测试注册，新增数据
	public void registerUser() {
		User user = new User();
		user.setAccount("admin");
		user.setUserName("admin");
		user.setEmail("itdragon@git.com");
		user.setIphone("12349857999");
		user.setPlainPassword("admin");
		user.setPlatform("github");
		user.setCreatedDate(CommonUtils.getCurrentDateTime());
		user.setUpdatedDate(CommonUtils.getCurrentDateTime());
        CommonUtils.entryptPassword(user);
		System.out.println(user);
		userService.registerUser(user);
	}

	@Autowired
	private SystemInfoService systemInfoService;

	@Test
	public  void  registerSystemInfo(){

		SystemInfo systemInfo = new SystemInfo();
		systemInfo.setAppCode("sso-client");
		systemInfo.setAppName("单点登录客户端");
		systemInfo.setRedirectUrl("http://SUNXIAOJUN.octopus.888ly.cn:8080/redirect");
		systemInfo.setLogoutUrl("http://SUNXIAOJUN.octopus.888ly.cn:8080/logout");
		systemInfo.setAdder("admin");
		systemInfo.setAddDate(CommonUtils.getCurrentDateTime());
		systemInfoService.register(systemInfo);
	}


	@Test
	public void getTimeOut(){
		System.out.println(new java.util.Date());
		System.out.println(CommonUtils.getTimeOut(7200));

	}

}
