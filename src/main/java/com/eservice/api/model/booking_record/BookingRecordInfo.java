package com.eservice.api.model.booking_record;

public class BookingRecordInfo extends BookingRecord {

    private String stuName;
    private String oldBusNumber;
    private String newBusNumber;
    private String stationName;

    public String getStuName() {
        return stuName;
    }

    public void setStuName(String stuName) {
        this.stuName = stuName;
    }

    public String getOldBusNumber() {
        return oldBusNumber;
    }

    public void setOldBusNumber(String oldBusNumber) {
        this.oldBusNumber = oldBusNumber;
    }

    public String getNewBusNumber() {
        return newBusNumber;
    }

    public void setNewBusNumber(String newBusNumber) {
        this.newBusNumber = newBusNumber;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }
}
