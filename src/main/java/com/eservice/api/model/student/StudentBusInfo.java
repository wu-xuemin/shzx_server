package com.eservice.api.model.student;

/**
 * 学生乘车模板
 */
public class StudentBusInfo extends Student{
    private String banjiName;
    private String busNumber;
    private Boolean morningAttendance;
    private Boolean afterAttendance;
    private Boolean nightAttendance;

    public Boolean isNightAttendance() {
        return nightAttendance;
    }

    public void setNightAttendance(Boolean nightAttendance) {
        this.nightAttendance = nightAttendance;
    }

    public String getBanjiName() {
        return banjiName;
    }

    public void setBanjiName(String banjiName) {
        this.banjiName = banjiName;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }

    public Boolean isMorningAttendance() {
        return morningAttendance;
    }

    public void setMorningAttendance(Boolean morningAttendance) {
        this.morningAttendance = morningAttendance;
    }

    public Boolean isAfterAttendance() {
        return afterAttendance;
    }

    public void setAfterAttendance(Boolean afterAttendance) {
        this.afterAttendance = afterAttendance;
    }

}
