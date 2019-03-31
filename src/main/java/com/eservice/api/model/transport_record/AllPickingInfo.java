package com.eservice.api.model.transport_record;

import com.eservice.api.model.student.Student;

import java.util.List;

public class AllPickingInfo {

    /**
     * StationPickingInfo：某站点的 名称，计划乘坐的学生List，实际乘坐的学生List
     */
    private List<StationPickingInfo> stationPickingInfoList;

    private String currentStation;


    public List<StationPickingInfo> getStationPickingInfoList() {
        return stationPickingInfoList;
    }

    public void setStationPickingInfoList(List<StationPickingInfo> stationPickingInfoList) {
        this.stationPickingInfoList = stationPickingInfoList;
    }

    public String getCurrentStation() {
        return currentStation;
    }

    public void setCurrentStation(String currentStation) {
        this.currentStation = currentStation;
    }
}