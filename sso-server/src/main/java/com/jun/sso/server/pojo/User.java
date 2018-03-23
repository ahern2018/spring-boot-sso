package com.jun.sso.server.pojo;

import com.jun.sso.server.utils.CommonUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * 用户实体类
 *
 * @author 孙小军
 */
@Table(name = "t_sso_user")
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;                        // 自增长主键

    @Column(length = 60)
    @NotNull
    private String account;                 // 登录的账号

    @Column(length = 120)
    @NotNull
    private String userName;                // 注册的昵称

    @Transient
    private String plainPassword;           // 登录时的密码，不持久化到数据库

    @NotNull
    private String password;                // 加密后的密码

    @NotNull
    private String salt;                    // 用于加密的盐

    @Column(length = 15)
    private String iphone;                    // 手机号

    @Column(length = 50)
    private String email;                    // 邮箱

    @Column(length = 60)
    private String platform;                // 用户来自的平台

    @Column(length = 20)
    private String createdDate;                // 用户注册时间

    @Column(length = 20)
    private String updatedDate;                // 用户最后一次登录时间

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPlainPassword() {
        return plainPassword;
    }

    public void setPlainPassword(String plainPassword) {
        this.plainPassword = plainPassword;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getIphone() {
        return iphone;
    }

    public void setIphone(String iphone) {
        this.iphone = iphone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", account=" + account + ", userName=" + userName + ", plainPassword=" + plainPassword
                + ", password=" + password + ", salt=" + salt + ", iphone=" + iphone + ", email=" + email
                + ", platform=" + platform + ", createdDate=" + createdDate + ", updatedDate=" + updatedDate + "]";
    }

    public User() {
    }

    public User(@NotNull String account, @NotNull String userName, String plainPassword, String iphone, String email, String platform) {
        this.account = account;
        this.userName = userName;
        this.plainPassword = plainPassword;
        this.iphone = iphone;
        this.email = email;
        this.platform = platform;
        this.createdDate = CommonUtils.getCurrentDateTime();
        this.updatedDate = CommonUtils.getCurrentDateTime();

    }
}
