package com.jun.sso.server.service;

import com.jun.sso.server.enums.LoginStatusEnum;
import com.jun.sso.server.pojo.BaseResponse;
import com.jun.sso.server.pojo.LoginSystem;
import com.jun.sso.server.pojo.User;
import com.jun.sso.server.repository.JedisClient;
import com.jun.sso.server.repository.LoginSystemRepository;
import com.jun.sso.server.repository.SystemInfoRepository;
import com.jun.sso.server.repository.UserRepository;
import com.jun.sso.server.utils.CommonUtils;
import com.jun.sso.server.utils.CookieUtils;
import com.jun.sso.server.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Date;
import java.util.UUID;

@Service
@Transactional
@PropertySource(value = "classpath:redis.properties")
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SystemInfoRepository systemInfoRepository;

    @Autowired
    private LoginSystemRepository loginSystemRepository;

    @Autowired
    private JedisClient jedisClient;

    @Value("${REDIS_USER_SESSION_KEY}")
    private String REDIS_USER_SESSION_KEY;

    @Value("${SSO_SESSION_EXPIRE}")
    private Integer SSO_SESSION_EXPIRE;

    @Value("${COOKIE_DOMAIN}")
    private String COOKIE_DOMAIN;


    /**
     * 验证Cookie是否存在，并验证Cookie是否超时
     *
     * @param request http请求
     * @return 返回BaseResponse
     */
    public BaseResponse validateCookie(String appCode, HttpServletRequest request) {

        //取Cookie
        String cookie = CookieUtils.getCookieValue(request, COOKIE_DOMAIN);

        // Cookie 不存在，则返回null
        if (CommonUtils.isNullOrEmpty(cookie)) {
            return BaseResponse.build(400, "当前没有登录信息，请登录");
        }
        // 根据token从redis中查询用户信息
        String json = jedisClient.get(REDIS_USER_SESSION_KEY + ":" + cookie);
        // 判断是否为空
        if (CommonUtils.isNullOrEmpty(json)) {
            return BaseResponse.build(400, "此session已经过期，请重新登录");
        }

        //记录系统登录信息
        LoginSystem loginSystem = new LoginSystem(appCode, cookie, LoginStatusEnum.LOGINING.getIndex());
        loginSystemRepository.save(loginSystem);

        //重新生成token
        String token = UUID.randomUUID().toString();
        // 把用户信息写入 redis
        jedisClient.set(REDIS_USER_SESSION_KEY + ":" + token, json);
        // 设置 session 的过期时间
        jedisClient.expire(REDIS_USER_SESSION_KEY + ":" + token, SSO_SESSION_EXPIRE);

        return BaseResponse.ok(token);

    }

    /**
     * 用户注册
     *
     * @param user 用户实体类
     * @return 返回BaseResponse
     */
    public BaseResponse registerUser(User user) {
        // 检查用户名是否注册，一般在前端验证的时候处理，因为注册不存在高并发的情况，这里再加一层查询是不影响性能的
        if (null != userRepository.findByAccount(user.getAccount())) {
            return BaseResponse.build(400, "用户账号已存在，请重新输入");
        }
        userRepository.save(user);
        // 注册成功后选择发送邮件激活。现在一般都是短信验证码
        return BaseResponse.build(200, "注册成功");
    }

    /**
     * 用户登录
     *
     * @param account  账号
     * @param password 密码
     * @param appCode  系统编号
     * @param request  Http请求
     * @param response Http返回
     * @return 返回BaseResponse
     */
    public BaseResponse userLogin(String account,
                                  String password,
                                  String appCode,
                                  HttpServletRequest request,
                                  HttpServletResponse response) throws Exception {
        // 判断账号密码是否正确
        User user = userRepository.findByAccount(account);
        if (user == null) {
            return BaseResponse.build(400, "账号名不存在");
        }
        if (!CommonUtils.decryptPassword(user, password)) {
            return BaseResponse.build(400, "账号名或密码错误");
        }
        // 生成token,只用于一次登录使用
        String token = UUID.randomUUID().toString();
        //生成cookie，用于永久保存
        String cookie = UUID.randomUUID().toString();

        // 清空密码和盐避免泄漏
        String userPassword = user.getPassword();
        String userSalt = user.getSalt();
        user.setPassword(null);
        user.setSalt(null);

        //记录系统登录信息
        LoginSystem loginSystem = new LoginSystem(appCode, cookie, LoginStatusEnum.LOGINING.getIndex());
        loginSystemRepository.save(loginSystem);

        //计算超时时间
        Date timeout = CommonUtils.getTimeOut(SSO_SESSION_EXPIRE);
        BaseResponse baseResponse = BaseResponse.ok(user, timeout);

        // 把用户信息写入 redis
        jedisClient.set(REDIS_USER_SESSION_KEY + ":" + token, JsonUtils.objectToJson(baseResponse));
        jedisClient.set(REDIS_USER_SESSION_KEY + ":" + cookie, JsonUtils.objectToJson(baseResponse));
        // 设置 session 的过期时间
        jedisClient.expire(REDIS_USER_SESSION_KEY + ":" + token, SSO_SESSION_EXPIRE);
        jedisClient.expire(REDIS_USER_SESSION_KEY + ":" + cookie, SSO_SESSION_EXPIRE);

        // user 已经是持久化对象了，被保存在了session缓存当中，若user又重新修改了属性值，那么在提交事务时，此时 hibernate对象就会拿当前这个user对象和保存在session缓存中的user对象进行比较，如果两个对象相同，则不会发送update语句，否则，如果两个对象不同，则会发出update语句。
        user.setPassword(userPassword);
        user.setSalt(userSalt);

        // 添加写 cookie 的逻辑，cookie 的有效期是关闭浏览器就失效。
        CookieUtils.setCookie(request, response, COOKIE_DOMAIN, cookie);

        return BaseResponse.ok(token);
    }

    /**
     * 退出登录
     *
     * @param request
     * @param response
     */
    public BaseResponse logout(String appCode, HttpServletRequest request, HttpServletResponse response) {

        //取出从cookie信息
        String cookie = CookieUtils.getCookieValue(request, COOKIE_DOMAIN);

        //根据AppCode、Cookie修改状态
        if (!CommonUtils.isNullOrEmpty(appCode)) {
            loginSystemRepository.updateStatusByAppCodeAndAndCookie(CommonUtils.getCurrentDateTime(), LoginStatusEnum.LOGOUT.getIndex(), appCode, cookie);
        }
        //取下一条记录
        Pageable pageable = new PageRequest(0, 1);
        List<String> list = loginSystemRepository.findLogoutUrlByCookieAndStatus(LoginStatusEnum.LOGINING.getIndex(), cookie, pageable);
        if (list.size() == 0) {
            //删除 SSO中心session信息
            jedisClient.del(REDIS_USER_SESSION_KEY + ":" + cookie);
            //删除cookie信息
            CookieUtils.deleteCookie(request, response, COOKIE_DOMAIN);
            return BaseResponse.build(400, "单点登录执行完成");
        }
        return BaseResponse.ok(list.get(0));
    }

    /**
     * 验证Token有效性
     *
     * @param token token值
     * @return 返回BaseResponse
     */
    public BaseResponse validateToken(String token) {
        // 根据token从redis中查询用户信息
        String json = jedisClient.get(REDIS_USER_SESSION_KEY + ":" + token);
        // 判断是否为空
        if (CommonUtils.isNullOrEmpty(json)) {
            return BaseResponse.build(400, "此session已经过期，请重新登录");
        }
        //删除token记录
        jedisClient.del(REDIS_USER_SESSION_KEY + ":" + token);
        // 返回用户信息
        BaseResponse baseResponse = JsonUtils.jsonToPojo(json, BaseResponse.class);

        return BaseResponse.ok(baseResponse.getData(), baseResponse.getTimeout());
    }


    @Value("${REDIS_SYSTEM_SESSION_KEY}")
    private String REDIS_SYSTEM_SESSION_KEY;

    @Value("${SYSTEM_COOKIE_DOMAIN}")
    private String SYSTEM_COOKIE_DOMAIN;

    /**
     * 系统用户登录
     * @param account
     * @param password
     * @param request
     * @param response
     * @return
     */
    public BaseResponse validateUserInfo(String account, String password, HttpServletRequest request, HttpServletResponse response) {
        // 判断账号密码是否正确
        User user = userRepository.findByAccount(account);
        if (user == null) {
            return BaseResponse.build(400, "账号名不存在");
        }
        if (!CommonUtils.decryptPassword(user, password)) {
            return BaseResponse.build(400, "账号名或密码错误");
        }

        //写redis
        String cookie = UUID.randomUUID().toString();
        jedisClient.set(REDIS_SYSTEM_SESSION_KEY + ":" + cookie, JsonUtils.objectToJson(user));
        jedisClient.expire(REDIS_SYSTEM_SESSION_KEY + ":" + cookie, SSO_SESSION_EXPIRE);
        //写Cookie
        CookieUtils.setCookie(request, response, SYSTEM_COOKIE_DOMAIN, cookie);

        return BaseResponse.ok();
    }
}
