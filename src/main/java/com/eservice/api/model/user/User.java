package com.eservice.api.model.user;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String account;

    private String name;

    /**
     * 微信unionId，在没授权前是空的。
     */
    @Column(name = "wechatUnionId")
    private String wechatunionid;

    @Column(name = "role_id")
    private Integer roleId;

    private String password;

    /**
     * 头像保存位置
     */
    @Column(name = "head_image")
    private String headImage;

    /**
     * 电话
     */
    private String phone;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 是否在职 ， 1:在职 0:离职
     */
    private Integer valid;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return account
     */
    public String getAccount() {
        return account;
    }

    /**
     * @param account
     */
    public void setAccount(String account) {
        this.account = account;
    }

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取微信unionId，在没授权前是空的。
     *
     * @return wechatUnionId - 微信unionId，在没授权前是空的。
     */
    public String getWechatunionid() {
        return wechatunionid;
    }

    /**
     * 设置微信unionId，在没授权前是空的。
     *
     * @param wechatunionid 微信unionId，在没授权前是空的。
     */
    public void setWechatunionid(String wechatunionid) {
        this.wechatunionid = wechatunionid;
    }

    /**
     * @return role_id
     */
    public Integer getRoleId() {
        return roleId;
    }

    /**
     * @param roleId
     */
    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    /**
     * @return password
     */
//    public String getPassword() {
//        return password;
//    }

    /**
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取头像保存位置
     *
     * @return head_image - 头像保存位置
     */
    public String getHeadImage() {
        return headImage;
    }

    /**
     * 设置头像保存位置
     *
     * @param headImage 头像保存位置
     */
    public void setHeadImage(String headImage) {
        this.headImage = headImage;
    }

    /**
     * 获取电话
     *
     * @return phone - 电话
     */
    public String getPhone() {
        return phone;
    }

    /**
     * 设置电话
     *
     * @param phone 电话
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @return create_time
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取是否在职 ， 1:在职 0:离职
     *
     * @return valid - 是否在职 ， “1”:在职 “0”:离职
     */
    public Integer getValid() {
        return valid;
    }

    /**
     * 设置是否在职 ， “1”:在职 “0”:离职
     *
     * @param valid 是否在职 ， “1”:在职 “0”:离职
     */
    public void setValid(Integer valid) {
        this.valid = valid;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> auths = new ArrayList<>();
//        List<SysRole> roles = this.getRoles();
//        for (SysRole role : roles) {
//            auths.add(new SimpleGrantedAuthority(role.getName()));
//        }
        return auths;
    }

    @Override
    public String getPassword() {
        return this.password;
    }
    @Override
    public String getUsername() {
        return this.account;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


}