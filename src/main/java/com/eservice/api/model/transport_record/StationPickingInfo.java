package com.eservice.api.model.transport_record;

import com.alibaba.fastjson.annotation.JSONField;
import com.eservice.api.model.student.Student;
import com.eservice.api.model.student.StudentInfo;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

public class StationPickingInfo {

    private String stationName;

    /**
     * 该站点 计划接送的学生
     */
    private List<StudentInfo> planList;

    /**
     * 该站点 实际接送的学生
     */
    private List<StudentInfo> pickedList;

    /**
     * 该站点 等待下车的学生， 在放学下车时统计车上剩下的学生
     */
    private List<StudentInfo> remainList;

    public List<StudentInfo> getRemainList() {
        return remainList;
    }

    public void setRemainList(List<StudentInfo> remainList) {
        this.remainList = remainList;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public void setPlanList(List<StudentInfo> planList) {
        this.planList = planList;
    }

    public List<StudentInfo> getPlanList(){
        return this.planList;
    }

    public void setPickedList(List<StudentInfo> pickedList) {
        this.pickedList = pickedList;
    }

    public List<StudentInfo> getPickedList() {
        return pickedList;
    }

    private Integer stationId;

    public Integer getStationId() {
        return stationId;
    }

    public void setStationId(Integer stationId) {
        this.stationId = stationId;
    }
}