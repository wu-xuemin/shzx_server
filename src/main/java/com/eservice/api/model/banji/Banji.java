package com.eservice.api.model.banji;

import com.alibaba.fastjson.annotation.JSONField;
import javax.persistence.*;
import java.util.Date;

public class Banji {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 年级
     */
    private String grade;

    /**
     * 班级名称
     */
    @Column(name = "class_name")
    private String className;

    /**
     * 班主任
     */
    @Column(name = "charge_teacher")
    private Integer chargeTeacher;

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
     * 获取年级
     *
     * @return grade - 年级
     */
    public String getGrade() {
        return grade;
    }

    /**
     * 设置年级
     *
     * @param grade 年级
     */
    public void setGrade(String grade) {
        this.grade = grade;
    }

    /**
     * 获取班级名称
     *
     * @return class_name - 班级名称
     */
    public String getClassName() {
        return className;
    }

    /**
     * 设置班级名称
     *
     * @param className 班级名称
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * 获取班主任
     *
     * @return charge_teacher - 班主任
     */
    public Integer getChargeTeacher() {
        return chargeTeacher;
    }

    /**
     * 设置班主任
     *
     * @param chargeTeacher 班主任
     */
    public void setChargeTeacher(Integer chargeTeacher) {
        this.chargeTeacher = chargeTeacher;
    }

    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time")
    private Date createTime;

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_time")
    private Date updateTime;

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 上中url里解析到的classId
     */

    @Column(name = "class_id_from_url")
    private String classIdFromUrl;

    public String getClassIdFromUrl() {
        return classIdFromUrl;
    }

    public void setClassIdFromUrl(String classIdFromUrl) {
        this.classIdFromUrl = classIdFromUrl;
    }
}