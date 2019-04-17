package com.eservice.api.model.banji;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.models.auth.In;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

/**
 * 班级信息+班主任信息
 */
public class BanjiInfo extends Banji {

    /**
     * 老师账号
     */
    private String account;

    /**
     * 老师姓名
     */
    private String name;

    /**
     * 老师头像
     */
    private String headImage;

    /**
     * 老师电话
     */
    private String phone;

    private Integer valid;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeadImage() {
        return headImage;
    }

    public void setHeadImage(String headImage) {
        this.headImage = headImage;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getValid() {
        return valid;
    }

    public void setValid(Integer valid) {
        this.valid = valid;
    }
}