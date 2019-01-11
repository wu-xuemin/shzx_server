package com.eservice.api.model.student;

import javax.persistence.*;

public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 学号
     */
    @Column(name = "student_number")
    private String studentNumber;

    /**
     * 保存头像的URL
     */
    @Column(name = "head_img")
    private String headImg;

    /**
     * 姓名
     */
    private String name;

    /**
     * 班级,外键
     */
    private Integer banji;

    /**
     * 早班乘坐车的ID，外键
     */
    private Integer busLineMorning;

    /**
     * 午班乘坐车的ID，外键
     */
    private Integer busLineAfternoon;

    public Integer getBusLineAfternoon() {
        return busLineAfternoon;
    }

    public void setBusLineAfternoon(Integer busLineAfternoon) {
        this.busLineAfternoon = busLineAfternoon;
    }

    /**
     * 上午班车上车站点
     */
    @Column(name = "board_station_morning")
    private Integer boardStationMorning;

    /**
     * 下午班车下车站点
     */
    @Column(name = "board_station_afternoon")
    private Integer boardStationAfternoon;

    /**
     * 家庭信息 JSON
     */
    @Column(name = "family_info")
    private String familyInfo;

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
     * 获取学号
     *
     * @return student_number - 学号
     */
    public String getStudentNumber() {
        return studentNumber;
    }

    /**
     * 设置学号
     *
     * @param studentNumber 学号
     */
    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    /**
     * 获取保存头像的URL
     *
     * @return head_img - 保存头像的URL
     */
    public String getHeadImg() {
        return headImg;
    }

    /**
     * 设置保存头像的URL
     *
     * @param headImg 保存头像的URL
     */
    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    /**
     * 获取姓名
     *
     * @return name - 姓名
     */
    public String getName() {
        return name;
    }

    /**
     * 设置姓名
     *
     * @param name 姓名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取班级,外键
     *
     * @return banji - 班级,外键
     */
    public Integer getBanji() {
        return banji;
    }

    /**
     * 设置班级,外键
     *
     * @param banji 班级,外键
     */
    public void setBanji(Integer banji) {
        this.banji = banji;
    }

    /**
     * 获取早班乘坐车的ID，外键
     *
     * @return bus - 乘坐车的ID，外键
     */
    public Integer getBusLineMorning() {
        return busLineMorning;
    }

    /**
     * 设置乘坐车的ID，外键
     *
     * @param busLineMorning 乘坐车的ID，外键
     */
    public void setBusLineMorning(Integer busLineMorning) {
        this.busLineMorning = busLineMorning;
    }

    /**
     * 获取上午班车上车站点
     *
     * @return board_station_morning - 上午班车上车站点
     */
    public Integer getBoardStationMorning() {
        return boardStationMorning;
    }

    /**
     * 设置上午班车上车站点
     *
     * @param boardStationMorning 上午班车上车站点
     */
    public void setBoardStationMorning(Integer boardStationMorning) {
        this.boardStationMorning = boardStationMorning;
    }

    /**
     * 获取下午班车下车站点
     *
     * @return board_station_afternoon - 下午班车下车站点
     */
    public Integer getBoardStationAfternoon() {
        return boardStationAfternoon;
    }

    /**
     * 设置下午班车下车站点
     *
     * @param boardStationAfternoon 下午班车下车站点
     */
    public void setBoardStationAfternoon(Integer boardStationAfternoon) {
        this.boardStationAfternoon = boardStationAfternoon;
    }

    /**
     * 获取家庭信息 JSON
     *
     * @return family_info - 家庭信息 JSON
     */
    public String getFamilyInfo() {
        return familyInfo;
    }

    /**
     * 设置家庭信息 JSON
     *
     * @param familyInfo 家庭信息 JSON
     */
    public void setFamilyInfo(String familyInfo) {
        this.familyInfo = familyInfo;
    }
}