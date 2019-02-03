package com.eservice.api.model.student;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class StudentExcel extends Student {

    private String gradeName;

    private String BanjiName;

    private String bzrName;

    public String getBanjiName() {
        return BanjiName;
    }

    public void setBanjiName(String banjiName) {
        BanjiName = banjiName;
    }

    public String getGradeName() {
        return gradeName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }

    public String getBzrName() {
        return bzrName;
    }

    public void setBzrName(String gzrName) {
        this.bzrName = gzrName;
    }

}