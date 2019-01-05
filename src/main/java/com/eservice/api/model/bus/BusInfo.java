package com.eservice.api.model.bus;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class BusInfo extends  Bus {


    /**
     * 供应商名称
     */
    private String busSupplierName;

    public String getBusSupplierName() {
        return busSupplierName;
    }

    public void setBusSupplierName(String busSupplierName) {
        this.busSupplierName = busSupplierName;
    }

    /**
     * 早班区间名称，区间名称是头尾两个站点构成。
     */
    private String transportRangeMorningName;

    public String getTransportRangeMorningName() {
        return transportRangeMorningName;
    }

    public void setTransportRangeMorningName(String transportRangeMorningName) {
        this.transportRangeMorningName = transportRangeMorningName;
    }

    /**
     * 午班区间名称，区间名称是头尾两个站点构成。
     */
    private String transportRangeAfternoonName;

    public String getTransportRangeAfternoonName() {
        return transportRangeAfternoonName;
    }

    public void setTransportRangeAfternoonName(String transportRangeAfternoonName) {
        this.transportRangeAfternoonName = transportRangeAfternoonName;
    }

    /**
     * 巴士妈妈姓名
     */
    private String busMomName;

    public String getBusMomName() {
        return busMomName;
    }

    public void setBusMomName(String busMomName) {
        this.busMomName = busMomName;
    }

    /**
     * 司机姓名
     */
    private String busDriverName;

    public String getBusDriverName() {
        return busDriverName;
    }

    public void setBusDriverName(String busDriverName) {
        this.busDriverName = busDriverName;
    }
}