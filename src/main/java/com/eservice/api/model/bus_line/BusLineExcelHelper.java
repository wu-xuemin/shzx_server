package com.eservice.api.model.bus_line;

import com.google.common.base.Strings;

import javax.persistence.Table;
import java.security.PrivateKey;

public class BusLineExcelHelper extends BusLineInfo{
    /**
     * 校车站点时间
     */
    private String timeRemark;

    public String getTimeRemark() {
        return timeRemark;
    }

    public void setTimeRemark(String timeRemark) {
        this.timeRemark = timeRemark;
    }

    /**
     * 注意这个类里的字段有和其他地方重复的，这个类只是在excel解析时有用
     */
    private String stationName;

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    private String studentNumber;

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    private String studentName;

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    private String studentPhones;

    public String getStudentPhones() {
        return studentPhones;
    }

    public void setStudentPhones(String studentTels) {
        this.studentPhones = studentTels;
    }

    private String fareRate;

    public String getFareRate() {
        return fareRate;
    }

    public void setFareRate(String fareRate) {
        this.fareRate = fareRate;
    }
}