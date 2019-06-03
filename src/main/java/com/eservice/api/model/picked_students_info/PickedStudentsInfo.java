package com.eservice.api.model.picked_students_info;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;
import javax.persistence.*;

@Table(name = "picked_students_info")
public class PickedStudentsInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "transport_record_id")
    private Integer transportRecordId;

    /**
     * 上下车时间
     */
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @Column(name = "board_time")
    private Date boardTime;

    /**
     * 学生(用ID表示）
     */
    @Column(name = "student_id")
    private Integer studentId;

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
     * @return transport_record_id
     */
    public Integer getTransportRecordId() {
        return transportRecordId;
    }

    /**
     * @param transportRecordId
     */
    public void setTransportRecordId(Integer transportRecordId) {
        this.transportRecordId = transportRecordId;
    }

    /**
     * 获取上下车时间
     *
     * @return board_time - 上下车时间
     */
    public Date getBoardTime() {
        return boardTime;
    }

    /**
     * 设置上下车时间
     *
     * @param boardTime 上下车时间
     */
    public void setBoardTime(Date boardTime) {
        this.boardTime = boardTime;
    }

    /**
     * 获取学生(用ID表示）
     *
     * @return student_id - 学生(用ID表示）
     */
    public Integer getStudentId() {
        return studentId;
    }

    /**
     * 设置学生(用ID表示）
     *
     * @param studentId 学生(用ID表示）
     */
    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    /**
     * 1: 刷脸，2：手动 (处理刷脸没成功识别的情况)
     */
    @Column(name = "check_mode")
    private Integer checkMode;

    public Integer getCheckMode() {
        return checkMode;
    }

    public void setCheckMode(Integer checkMode) {
        this.checkMode = checkMode;
    }
}