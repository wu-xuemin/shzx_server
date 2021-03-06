package com.eservice.api.model.booking_record;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;
import javax.persistence.*;

@Table(name = "booking_record")
public class BookingRecord {
    /**
     * 该表只是记录变更历史，实际的变更在确认后，应该更新到实际的表
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 变更线路的日期
     */
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @Column(name = "change_date")
    private Date changeDate;

    /**
     * 要变更线路的学生
     */
    private Integer student;

    /**
     * 原路线
     */
    @Column(name = "old_bus_line")
    private Integer oldBusLine;

    /**
     * 新路线
     */
    @Column(name = "new_bus_line")
    private Integer newBusLine;

    /**
     * 新站点
     */
    @Column(name = "new_station")
    private Integer newStation;

    /**
     * 变更内容
     */
    @Column(name = "change_content")
    private String changeContent;

    /**
     * 是否确认
     */
    @Column(name = "confirm_status")
    private String confirmStatus;

    /**
     * 该条记录的创建时间
     */
    @JSONField (format="yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 该条记录的更新时间
     */
    @JSONField (format="yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 获取该表只是记录变更历史，实际的变更在确认后，应该更新到实际的表
     *
     * @return id - 该表只是记录变更历史，实际的变更在确认后，应该更新到实际的表
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置该表只是记录变更历史，实际的变更在确认后，应该更新到实际的表
     *
     * @param id 该表只是记录变更历史，实际的变更在确认后，应该更新到实际的表
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取变更线路的日期
     *
     * @return change_date - 变更线路的日期
     */
    public Date getChangeDate() {
        return changeDate;
    }

    /**
     * 设置变更线路的日期
     *
     * @param changeDate 变更线路的日期
     */
    public void setChangeDate(Date changeDate) {
        this.changeDate = changeDate;
    }

    /**
     * 获取要变更线路的学生
     *
     * @return student - 要变更线路的学生
     */
    public Integer getStudent() {
        return student;
    }

    /**
     * 设置要变更线路的学生
     *
     * @param student 要变更线路的学生
     */
    public void setStudent(Integer student) {
        this.student = student;
    }

    /**
     * 获取原校车路线
     *
     * @return old_bus_line - 原校车路线
     */
    public Integer getOldBusLine() {
        return oldBusLine;
    }

    /**
     * 设置原校车路线路线
     *
     * @param oldBusLine 原校车路线
     */
    public void setOldBusLine(Integer oldBusLine) {
        this.oldBusLine = oldBusLine;
    }

    /**
     * 获取新校车路线
     *
     * @return new_bus_line - 新校车路线
     */
    public Integer getNewBusLine() {
        return newBusLine;
    }

    /**
     * 设置新校车路线
     *
     * @param newBusLine 新校车路线
     */
    public void setNewBusLine(Integer newBusLine) {
        this.newBusLine = newBusLine;
    }

    /**
     * 获取新站点
     *
     * @return new_station - 新站点
     */
    public Integer getNewStation() {
        return newStation;
    }

    /**
     * 设置新站点
     *
     * @param newStation 新站点
     */
    public void setNewStation(Integer newStation) {
        this.newStation = newStation;
    }

    /**
     * 获取变更内容
     *
     * @return change_content - 变更内容
     */
    public String getChangeContent() {
        return changeContent;
    }

    /**
     * 设置变更内容
     *
     * @param changeContent 变更内容
     */
    public void setChangeContent(String changeContent) {
        this.changeContent = changeContent;
    }

    /**
     * 获取是否确认
     *
     * @return confirm_status - 是否确认
     */
    public String getConfirmStatus() {
        return confirmStatus;
    }

    /**
     * 设置是否确认
     *
     * @param confirmStatus 是否确认
     */
    public void setConfirmStatus(String confirmStatus) {
        this.confirmStatus = confirmStatus;
    }

    /**
     * 获取该条记录的创建时间
     *
     * @return create_time - 该条记录的创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置该条记录的创建时间
     *
     * @param createTime 该条记录的创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取该条记录的更新时间
     *
     * @return update_time - 该条记录的更新时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置该条记录的更新时间
     *
     * @param updateTime 该条记录的更新时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}