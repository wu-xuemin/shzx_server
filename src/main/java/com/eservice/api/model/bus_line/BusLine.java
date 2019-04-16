package com.eservice.api.model.bus_line;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.util.Date;

@Table(name = "bus_line")
public class BusLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 校车基本信息
     */
    @Column(name = "bus_base_info")
    private Integer busBaseInfo;

    /**
     * "上学“、”放学“、”晚班“
     */
    private String mode;

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
     * 获取校车基本信息
     *
     * @return bus_base_info - 校车基本信息
     */
    public Integer getBusBaseInfo() {
        return busBaseInfo;
    }

    /**
     * 设置校车基本信息
     *
     * @param busBaseInfo 校车基本信息
     */
    public void setBusBaseInfo(Integer busBaseInfo) {
        this.busBaseInfo = busBaseInfo;
    }

    /**
     * 获取"上学“、”放学“、”晚班“
     *
     * @return mode - "上学“、”放学“、”晚班“
     */
    public String getMode() {
        return mode;
    }

    /**
     * 设置"上学“、”放学“、”晚班“
     *
     * @param mode "上学“、”放学“、”晚班“
     */
    public void setMode(String mode) {
        this.mode = mode;
    }

    private String stations;

    /**
     * 某校车，比如沪A34567，早上的车号是“1”，放学，晚班的车号也是“1”.
     * 线路的名称则为 1号车_上学、1号车_放学。
     */
    private String name;

    private Integer valid;

    public String getStations() {
        return stations;
    }

    public void setStations(String stations) {
        this.stations = stations;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getValid() {
        return valid;
    }

    public void setValid(Integer valid) {
        this.valid = valid;
    }

    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time")
    private Date createTime;

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_time")
    private Date updateTime;

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}