package com.eservice.api.model.banji;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class BanjiExcel {

    private String grade;

    private String className;

    //班主任姓名
    private String chargeTeacherName;
    //班主任工号
    private String chargeTeacherCode;

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getChargeTeacherName() {
        return chargeTeacherName;
    }

    public void setChargeTeacherName(String chargeTeacherName) {
        this.chargeTeacherName = chargeTeacherName;
    }

    public String getChargeTeacherCode() {
        return chargeTeacherCode;
    }

    public void setChargeTeacherCode(String chargeTeacherCode) {
        this.chargeTeacherCode = chargeTeacherCode;
    }

}