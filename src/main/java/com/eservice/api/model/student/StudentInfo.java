package com.eservice.api.model.student;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

public class StudentInfo extends Student {

    private String banjiName;

    public String getBanjiName() {
        return banjiName;
    }

    public void setBanjiName(String banjiName) {
        this.banjiName = banjiName;
    }

    private String busNumber;

    public String getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }

    private String boardStationMorningName;

    public void setBoardStationMorningName(String boardStationMorningName) {
        this.boardStationMorningName = boardStationMorningName;
    }

    public String getBoardStationMorningName() {
        return boardStationMorningName;
    }

    private String boardStationAfternoonName;

    public String getBoardStationAfternoonName() {
        return boardStationAfternoonName;
    }

    public void setBoardStationAfternoonName(String boardStationAfternoonName) {
        this.boardStationAfternoonName = boardStationAfternoonName;
    }

    private String busPlateNumber;

    private String chargeTeacherName;

    private String chargeTeacherPhone;

    public Date getBoardTime() {
        return boardTime;
    }

    public void setBoardTime(Date boardTime) {
        this.boardTime = boardTime;
    }

    /**
     * 上或下车时间
     */
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date boardTime;

    public String getBusPlateNumber() {
        return busPlateNumber;
    }

    public void setBusPlateNumber(String busPlateNumber) {
        this.busPlateNumber = busPlateNumber;
    }

    public String getChargeTeacherName() {
        return chargeTeacherName;
    }

    public void setChargeTeacherName(String chargeTeacherName) {
        this.chargeTeacherName = chargeTeacherName;
    }

    public String getChargeTeacherPhone() {
        return chargeTeacherPhone;
    }

    public void setChargeTeacherPhone(String chargeTeacherPhone) {
        this.chargeTeacherPhone = chargeTeacherPhone;
    }
}
