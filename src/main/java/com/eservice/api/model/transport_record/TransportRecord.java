package com.eservice.api.model.transport_record;

import java.util.Date;
import javax.persistence.*;

@Table(name = "transport_record")
public class TransportRecord {
    /**
     * 接送信息
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 接送日期  TODO,这个可以删掉
     */
    private Date date;

    /**
     * 接送模式，分为“上午接”，“下午送”，“晚班”
     */
//    private String mode;

    /**
     * 校车线路，外键，可以根据校车去查应乘学生
     */
    private Integer busLine;

    /**
     * 校车所处的当前站点
     */
    @Column(name = "current_station")
    private Integer currentStation;

    /**
     * 获取接送信息
     *
     * @return id - 接送信息
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置接送信息
     *
     * @param id 接送信息
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取接送日期
     *
     * @return date - 接送日期
     */
    public Date getDate() {
        return date;
    }

    /**
     * 设置接送日期
     *
     * @param date 接送日期
     */
    public void setDate(Date date) {
        this.date = date;
    }

//    /**
//     * 获取接送模式，分为“上午接”，“下午送”，“晚班”
//     *
//     * @return mode - 接送模式，分为“上午接”，“下午送”，“晚班”
//     */
//    public String getMode() {
//        return mode;
//    }

    /**
     * 设置接送模式，分为“上午接”，“下午送”，“晚班”
     *
     * @param mode 接送模式，分为“上午接”，“下午送”，“晚班”
     */
//    public void setMode(String mode) {
//        this.mode = mode;
//    }

    /**
     * 获取校车，外键，可以根据校车去查应乘学生
     *
     * @return bus - 校车，外键，可以根据校车去查应乘学生
     */
    public Integer getBusLine() {
        return busLine;
    }

    /**
     * 设置校车，外键，可以根据校车去查应乘学生
     *
     * @param busLine 校车，外键，可以根据校车去查应乘学生
     */
    public void setBusLine(Integer busLine) {
        this.busLine = busLine;
    }

    /**
     * 获取校车所处的当前站点
     *
     * @return current_station - 校车所处的当前站点
     */
    public Integer getCurrentStation() {
        return currentStation;
    }

    /**
     * 设置校车所处的当前站点
     *
     * @param currentStation 校车所处的当前站点
     */
    public void setCurrentStation(Integer currentStation) {
        this.currentStation = currentStation;
    }
}