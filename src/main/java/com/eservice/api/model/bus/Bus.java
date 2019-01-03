package com.eservice.api.model.bus;

import javax.persistence.*;

public class Bus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 校车编号
     */
    private String number;

    /**
     * 车牌号
     */
    @Column(name = "plate_number")
    private String plateNumber;

    /**
     * 牌照照片存放路径
     */
    @Column(name = "plate_number_pic")
    private String plateNumberPic;

    /**
     * 供应商，外键
     */
    @Column(name = "bus_supplier")
    private Integer busSupplier;

    /**
     * 早班区间，
     */
    @Column(name = "transport_range_morning")
    private Integer transportRangeMorning;

    /**
     * 午班区间，
     */
    @Column(name = "transport_range_afternoon")
    private Integer transportRangeAfternoon;

    /**
     * 巴士妈妈，外键
     */
    @Column(name = "bus_mom")
    private Integer busMom;

    /**
     * 司机
     */
    @Column(name = "bus_driver")
    private Integer busDriver;

    /**
     * 浦东校区；浦西校区
     */
    @Column(name = "school_partition")
    private String schoolPartition;

    /**
     * ipad绑定的设备号
     */
    @Column(name = "ipad_meid")
    private String ipadMeid;

    private Byte valid;

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
     * 获取校车编号
     *
     * @return number - 校车编号
     */
    public String getNumber() {
        return number;
    }

    /**
     * 设置校车编号
     *
     * @param number 校车编号
     */
    public void setNumber(String number) {
        this.number = number;
    }

    /**
     * 获取车牌号
     *
     * @return plate_number - 车牌号
     */
    public String getPlateNumber() {
        return plateNumber;
    }

    /**
     * 设置车牌号
     *
     * @param plateNumber 车牌号
     */
    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    /**
     * 获取牌照照片存放路径
     *
     * @return plate_number_pic - 牌照照片存放路径
     */
    public String getPlateNumberPic() {
        return plateNumberPic;
    }

    /**
     * 设置牌照照片存放路径
     *
     * @param plateNumberPic 牌照照片存放路径
     */
    public void setPlateNumberPic(String plateNumberPic) {
        this.plateNumberPic = plateNumberPic;
    }

    /**
     * 获取供应商，外键
     *
     * @return bus_supplier - 供应商，外键
     */
    public Integer getBusSupplier() {
        return busSupplier;
    }

    /**
     * 设置供应商，外键
     *
     * @param busSupplier 供应商，外键
     */
    public void setBusSupplier(Integer busSupplier) {
        this.busSupplier = busSupplier;
    }

    /**
     * 获取早班区间，
     *
     * @return transport_range - 早班区间，
     */
    public Integer getTransportRangeMorning() {
        return transportRangeMorning;
    }

    /**
     * 设置早班区间，
     *
     * @param transportRangeMorning 早班区间，
     */
    public void setTransportRangeMorning(Integer transportRangeMorning) {
        this.transportRangeMorning = transportRangeMorning;
    }

    /**
     * 获取午班区间，
     *
     * @return transport_range - 午班区间，
     */
    public Integer getTransportRangeAfternoon() {
        return transportRangeAfternoon;
    }

    /**
     * 设置午班区间，
     *
     * @param transportRangeMorningAfternoon 午班区间，
     */
    public void setTransportRangeAfternoon(Integer transportRangeMorningAfternoon) {
        this.transportRangeAfternoon = transportRangeMorningAfternoon;
    }




    /**
     * 获取巴士妈妈，外键
     *
     * @return bus_mom - 巴士妈妈，外键
     */
    public Integer getBusMom() {
        return busMom;
    }

    /**
     * 设置巴士妈妈，外键
     *
     * @param busMom 巴士妈妈，外键
     */
    public void setBusMom(Integer busMom) {
        this.busMom = busMom;
    }

    /**
     * 获取司机
     *
     * @return bus_driver - 司机
     */
    public Integer getBusDriver() {
        return busDriver;
    }

    /**
     * 设置司机
     *
     * @param busDriver 司机
     */
    public void setBusDriver(Integer busDriver) {
        this.busDriver = busDriver;
    }

    /**
     * 获取浦东校区；浦西校区
     *
     * @return school_partition - 浦东校区；浦西校区
     */
    public String getSchoolPartition() {
        return schoolPartition;
    }

    /**
     * 设置浦东校区；浦西校区
     *
     * @param schoolPartition 浦东校区；浦西校区
     */
    public void setSchoolPartition(String schoolPartition) {
        this.schoolPartition = schoolPartition;
    }

    /**
     * 获取ipad绑定的设备号
     *
     * @return ipad_meid - ipad绑定的设备号
     */
    public String getIpadMeid() {
        return ipadMeid;
    }

    /**
     * 设置ipad绑定的设备号
     *
     * @param ipadMeid ipad绑定的设备号
     */
    public void setIpadMeid(String ipadMeid) {
        this.ipadMeid = ipadMeid;
    }

    /**
     * @return valid
     */
    public Byte getValid() {
        return valid;
    }

    /**
     * @param valid
     */
    public void setValid(Byte valid) {
        this.valid = valid;
    }

    /**
     * 接送模式，分为“上午接送”，“下午接送”，“晚班” 这个字段如果放在transport_record里，则无法统计某车该乘车的学生列表，也就无法统计缺乘列表.
     * 特别注意：同一辆车，接送模式不同，就当作不同的车。也就是说，实际上这个表格的车辆数是实际车数的3倍（一辆车分为上午接送，下午接送，晚班）。
     */
    @Column(name = "mode")
    private String mode;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}