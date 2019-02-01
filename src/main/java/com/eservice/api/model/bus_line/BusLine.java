package com.eservice.api.model.bus_line;

import javax.persistence.*;

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
     * "早班“、”午班“、”晚班“
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
     * 获取"早班“、”午班“、”晚班“
     *
     * @return mode - "早班“、”午班“、”晚班“
     */
    public String getMode() {
        return mode;
    }

    /**
     * 设置"早班“、”午班“、”晚班“
     *
     * @param mode "早班“、”午班“、”晚班“
     */
    public void setMode(String mode) {
        this.mode = mode;
    }

    private String stations;

    /**
     * 某校车，比如沪A34567，早上的车号是“1”，午班，晚班的车号也是“1”.
     * 线路的名称则为 1号车_早班、1号车_午班。
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
}