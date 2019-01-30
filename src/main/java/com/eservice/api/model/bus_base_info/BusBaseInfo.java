package com.eservice.api.model.bus_base_info;

import javax.persistence.*;

@Table(name = "bus_base_info")
public class BusBaseInfo {
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
     * 供应商
     */
    @Column(name = "bus_supplier")
    private String busSupplier;

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
    public String getBusSupplier() {
        return busSupplier;
    }

    /**
     * 设置供应商，外键
     *
     * @param busSupplier 供应商，外键
     */
    public void setBusSupplier(String busSupplier) {
        this.busSupplier = busSupplier;
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
}