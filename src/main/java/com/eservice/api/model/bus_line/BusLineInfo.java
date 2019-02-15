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
     * 巴士妈妈账号(一般就是名字）
     */
    private String busMomAccount;

    public void setBusMomAccount(String busMomAccount) {
        this.busMomAccount = busMomAccount;
    }

    public String getBusMomAccount() {
        return busMomAccount;
    }

    private String busMomName;

    private String busMomPhone;

    private String busMomHeadImg;

    public String getBusMomName() {
        return busMomName;
    }

    public void setBusMomName(String busMomName) {
        this.busMomName = busMomName;
    }

    public String getBusMomPhone() {
        return busMomPhone;
    }

    public void setBusMomPhone(String busMomPhone) {
        this.busMomPhone = busMomPhone;
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

    public String busDriverName;

    public String busDriverPhone;

    public String getBusMomHeadImg() {
        return busMomHeadImg;
    }

    public void setBusMomHeadImg(String busMomHeadImg) {
        this.busMomHeadImg = busMomHeadImg;
    }

    public String getBusDriverHeadImg() {
        return busDriverHeadImg;
    }

    public void setBusDriverHeadImg(String busDriverHeadImg) {
        this.busDriverHeadImg = busDriverHeadImg;
    }

    private String busDriverHeadImg;

    public String getBusDriverName() {
        return busDriverName;
    }

    public void setBusDriverName(String busDriverName) {
        this.busDriverName = busDriverName;
    }

    public String getBusDriverPhone() {
        return busDriverPhone;
    }

    public void setBusDriverPhone(String busDriverPhone) {
        this.busDriverPhone = busDriverPhone;
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

//    private Byte valid;
//
//    public void setValid(Byte valid) {
//        this.valid = valid;
//    }
//
//    public Byte getValid() {
//        return valid;
//    }

    private String busSupplier;

    public String getBusSupplier() {
        return busSupplier;
    }

    public void setBusSupplier(String busSupplier) {
        this.busSupplier = busSupplier;
    }
}