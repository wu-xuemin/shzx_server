package com.eservice.api.model.bus_stations;

import javax.persistence.*;
import java.sql.Time;

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

    // todo 时间类型是否合适
    private Time remark;

    private Integer valid;

    public String getFareRate() {
        return fareRate;
    }

    public void setFareRate(String fareRate) {
        this.fareRate = fareRate;
    }

    public Time getRemark() {
        return remark;
    }

    public void setRemark(Time remark) {
        this.remark = remark;
    }

    public Integer getValid() {
        return valid;
    }

    public void setValid(Integer valid) {
        this.valid = valid;
    }
}