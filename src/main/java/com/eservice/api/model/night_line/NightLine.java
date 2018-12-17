package com.eservice.api.model.night_line;

import javax.persistence.*;

@Table(name = "night_line")
public class NightLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 线路号
     */
    @Column(name = "line_number")
    private String lineNumber;

    /**
     * 站点名称，JSON
     */
    private String stations;

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
     * 获取线路号
     *
     * @return line_number - 线路号
     */
    public String getLineNumber() {
        return lineNumber;
    }

    /**
     * 设置线路号
     *
     * @param lineNumber 线路号
     */
    public void setLineNumber(String lineNumber) {
        this.lineNumber = lineNumber;
    }

    /**
     * 获取站点名称，JSON
     *
     * @return stations - 站点名称，JSON
     */
    public String getStations() {
        return stations;
    }

    /**
     * 设置站点名称，JSON
     *
     * @param stations 站点名称，JSON
     */
    public void setStations(String stations) {
        this.stations = stations;
    }
}