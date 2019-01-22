package com.eservice.api.service.impl;

import com.eservice.api.dao.BusStationsMapper;
import com.eservice.api.model.bus_stations.BusStations;
import com.eservice.api.service.BusStationsService;
import com.eservice.api.core.AbstractService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
* Class Description: xxx
* @author Wilson Hu
* @date 2018/12/17.
*/
@Service
@Transactional
public class BusStationsServiceImpl extends AbstractService<BusStations> implements BusStationsService {
    @Resource
    private BusStationsMapper busStationsMapper;

    public BusStations getBusStation(String stationName){
        return busStationsMapper.getBusStation(stationName);
    }

}
