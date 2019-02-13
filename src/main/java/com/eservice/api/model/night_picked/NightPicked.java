package com.eservice.api.model.night_picked;

import java.util.Date;
import javax.persistence.*;

@Table(name = "night_picked")
public class NightPicked {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "student_number")
    private String studentNumber;

    @Column(name = "bus_number")
    private String busNumber;

    @Column(name = "bus_line")
    private Integer busLine;

    private Date date;

    @Column(name = "create_time")
    private Date createTime;

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
     * @return student_number
     */
    public String getStudentNumber() {
        return studentNumber;
    }

    /**
     * @param studentNumber
     */
    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    /**
     * @return bus_number
     */
    public String getBusNumber() {
        return busNumber;
    }

    /**
     * @param busNumber
     */
    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }

    /**
     * @return bus_line
     */
    public Integer getBusLine() {
        return busLine;
    }

    /**
     * @param busLine
     */
    public void setBusLine(Integer busLine) {
        this.busLine = busLine;
    }

    /**
     * @return date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @return create_time
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}