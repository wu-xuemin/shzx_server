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
     * 班车区间，包含了夜班路线
     */
    @Column(name = "transport_range")
    private Integer transportRange;

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
     * 获取班车区间，包含了夜班路线
     *
     * @return transport_range - 班车区间，包含了夜班路线
     */
    public Integer getTransportRange() {
        return transportRange;
    }

    /**
     * 设置班车区间，包含了夜班路线
     *
     * @param transportRange 班车区间，包含了夜班路线
     */
    public void setTransportRange(Integer transportRange) {
        this.transportRange = transportRange;
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
}