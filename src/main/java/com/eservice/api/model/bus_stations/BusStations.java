package com.eservice.api.model.bus_stations;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.sql.Time;
import java.util.Date;

@Table(name = "bus_stations")
public class BusStations {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 站点名称
     */
    @Column(name = "station_name")
    private String stationName;

    @Column(name = "gps_info")
    private String gpsInfo;

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
     * 获取站点名称
     *
     * @return station_name - 站点名称
     */
    public String getStationName() {
        return stationName;
    }

    /**
     * 设置站点名称
     *
     * @param stationName 站点名称
     */
    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    /**
     * @return gps_info
     */
    public String getGpsInfo() {
        return gpsInfo;
    }

    /**
     * @param gpsInfo
     */
    public void setGpsInfo(String gpsInfo) {
        this.gpsInfo = gpsInfo;
    }

    /**
     * 收费信息
     */
    private String fareRate;

    private String remark;

    private Integer valid;

    public String getFareRate() {
        return fareRate;
    }

    public void setFareRate(String fareRate) {
        this.fareRate = fareRate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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