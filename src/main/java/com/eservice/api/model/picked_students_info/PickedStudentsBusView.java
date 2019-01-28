package com.eservice.api.model.picked_students_info;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.util.Date;

public class PickedStudentsBusView extends PickedStudentsInfo {
    public Integer getBusLine() {
        return busLine;
    }

    public void setBusLine(Integer busLine) {
        this.busLine = busLine;
    }

    public Integer getCurrentStation() {
        return currentStation;
    }

    public void setCurrentStation(Integer currentStation) {
        this.currentStation = currentStation;
    }

    public String getCurrentStationName() {
        return currentStationName;
    }

    public void setCurrentStationName(String currentStationName) {
        this.currentStationName = currentStationName;
    }

    public String getModeName() {
        return modeName;
    }

    public void setModeName(String modeName) {
        this.modeName = modeName;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

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

    public String getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }

    public String getBusValid() {
        return busValid;
    }

    public void setBusValid(String busValid) {
        this.busValid = busValid;
    }

    private Integer busLine;
    private Integer currentStation;
//    private String stationName;
    private String currentStationName;
    private String modeName;

    private String busLineStations;
    private String busLineName;

    public String getBusLineStations() {
        return busLineStations;
    }

    public void setBusLineStations(String busLineStations) {
        this.busLineStations = busLineStations;
    }

    public String getBusLineName() {
        return busLineName;
    }

    public void setBusLineName(String busLineName) {
        this.busLineName = busLineName;
    }

    private String studentName;
    private String studentNumber;
    private String headImg;
    private String grade;
    private String className;
    private String busNumber;
    private String busValid;
}