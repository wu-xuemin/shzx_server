package com.eservice.api.model.transport_record;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

public class TransportRecordInfo extends  TransportRecord {

    // 注意： 每个学生，每次上下车都对应1个记录

    /**
     * 校车编号
     */
    private String busNumber;

    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }

    public String getBusNumber() {
        return busNumber;
    }

    /**
     * 早班 上车或下车站点名称，注意，在午班记录里，早班的站点名字为空
     */
    private String boardStationNameMorning;

    public String getBoardStationNameMorning() {
        return boardStationNameMorning;
    }

    public void setBoardStationNameMorning(String boardStationNameMorning) {
        this.boardStationNameMorning = boardStationNameMorning;
    }

    /**
     * 午班的 上车或下车站点名称，注意，在早班记录里，午班的站点名字为空
     */
    private String boardStationNameAfternoon;

    public String getBoardStationNameAfternoon() {
        return boardStationNameAfternoon;
    }

    public void setBoardStationNameAfternoon(String boardStationNameAfternoon) {
        this.boardStationNameAfternoon = boardStationNameAfternoon;
    }

    /**
     * 上或下车时间
     */
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date boardTime;

    public Date getBoardTime() {
        return boardTime;
    }

    public void setBoardTime(Date boardTime) {
        this.boardTime = boardTime;
    }

    /**
     * 学生所在班级
     */
    private String studentBanji;

    public String getStudentBanji() {
        return studentBanji;
    }

    public void setStudentBanji(String studentBanji) {
        this.studentBanji = studentBanji;
    }

    /**
     * 学生的学号
     */
    private String studentNumber;

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    /**
     * 学生姓名
     */
    private String studentName;

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    /**
     * 年级
     */
    private String studentGrade;

    public String getStudentGrade() {
        return studentGrade;
    }

    public void setStudentGrade(String grade) {
        this.studentGrade = grade;
    }

    private String mode;

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }

    private String currentStationName;

    public String getCurrentStationName() {
        return currentStationName;
    }

    public void setCurrentStationName(String currentStationName) {
        this.currentStationName = currentStationName;
    }

    /**
     * 该线路的各个站点
     */
    private String busLineStations;

    public String getBusLineStations() {
        return busLineStations;
    }

    public void setBusLineStations(String busLineStations) {
        this.busLineStations = busLineStations;
    }

    /**
     * 该线路的名称
     */
    private String busLineName;

    public String getBusLineName() {
        return busLineName;
    }

    public void setBusLineName(String busLineName) {
        this.busLineName = busLineName;
    }

    private String studentHeadImg;

    public String getStudentHeadImg() {
        return studentHeadImg;
    }

    public void setStudentHeadImg(String studentHeadImg) {
        this.studentHeadImg = studentHeadImg;
    }
}