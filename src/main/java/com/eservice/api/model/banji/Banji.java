package com.eservice.api.model.banji;

import javax.persistence.*;

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
}