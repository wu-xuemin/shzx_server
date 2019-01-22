package com.eservice.api.dao;

import com.eservice.api.core.Mapper;
import com.eservice.api.model.bus_stations.BusStations;
import org.apache.ibatis.annotations.Param;

public interface BusStationsMapper extends Mapper<BusStations> {

   BusStations getBusStation(@Param("stationName")String stationName);
}