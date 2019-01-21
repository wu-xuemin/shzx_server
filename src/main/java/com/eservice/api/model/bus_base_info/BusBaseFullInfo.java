package com.eservice.api.model.bus_base_info;

import javax.persistence.*;

public class BusBaseFullInfo extends BusBaseInfo {

    private String busRangeName;

    public String getBusRangeName() {
        return busRangeName;
    }

    public void setBusRangeName(String busRangeName) {
        this.busRangeName = busRangeName;
    }

    private String busMomName;

    public void setBusMomName(String busMomName) {
        this.busMomName = busMomName;
    }

    public String getBusMomName() {
        return busMomName;
    }

    private String busMomAccount;

    public String getBusMomAccount() {
        return busMomAccount;
    }

    public void setBusMomAccount(String busMomAccount) {
        this.busMomAccount = busMomAccount;
    }

    private String busDriverName;

    public String getBusDriverName() {
        return busDriverName;
    }

    public void setBusDriverName(String busDriverName) {
        this.busDriverName = busDriverName;
    }

    private String busDriverAccount;

    public String getBusDriverAccount() {
        return busDriverAccount;
    }

    public void setBusDriverAccount(String busDriverAccount) {
        this.busDriverAccount = busDriverAccount;
    }
}