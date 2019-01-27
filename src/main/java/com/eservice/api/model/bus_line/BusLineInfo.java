package com.eservice.api.model.bus_line;

import javax.persistence.*;

@Table(name = "bus_line")
public class BusLineInfo extends BusLine{
    /**
     * 校车编号
     */
    private String busNumber;

    public String getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }

    /**
     * 车牌号
     */
    private String plateNumber;

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    /**
     * 牌照照片存放路径
     */
    private String plateNumberPic;

    public String getPlateNumberPic() {
        return plateNumberPic;
    }

    public void setPlateNumberPic(String plateNumberPic) {
        this.plateNumberPic = plateNumberPic;
    }

    /**
     * 供应商名字
     */
    private String busSupplierName;

    public void setBusSupplierName(String busSupplierName) {
        this.busSupplierName = busSupplierName;
    }

    public String getBusSupplierName() {
        return busSupplierName;
    }

    /**
     * 巴士妈妈账号(一般就是名字）
     */
    private String busMomAccount;

    public void setBusMomAccount(String busMomAccount) {
        this.busMomAccount = busMomAccount;
    }

    public String getBusMomAccount() {
        return busMomAccount;
    }

    /**
     * 司机账号
     */
    private String busDriverAccount;

    public void setBusDriverAccount(String busDriverAccount) {
        this.busDriverAccount = busDriverAccount;
    }

    public String getBusDriverAccount() {
        return busDriverAccount;
    }

    /**
     * 浦东校区；浦西校区
     */
    private String schoolPartition;

    public void setSchoolPartition(String schoolPartition) {
        this.schoolPartition = schoolPartition;
    }

    public String getSchoolPartition() {
        return schoolPartition;
    }

    /**
     * ipad绑定的设备号
     */
    private String ipadMeid;

    public void setIpadMeid(String ipadMeid) {
        this.ipadMeid = ipadMeid;
    }

    public String getIpadMeid() {
        return ipadMeid;
    }

    private Byte valid;

    public void setValid(Byte valid) {
        this.valid = valid;
    }

    public Byte getValid() {
        return valid;
    }
}