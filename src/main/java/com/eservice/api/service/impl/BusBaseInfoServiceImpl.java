package com.eservice.api.service.impl;

import com.eservice.api.dao.BusBaseInfoMapper;
import com.eservice.api.model.bus_base_info.BusBaseFullInfo;
import com.eservice.api.model.bus_base_info.BusBaseInfo;
import com.eservice.api.service.BusBaseInfoService;
import com.eservice.api.core.AbstractService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;


/**
* Class Description: xxx
* @author Wilson Hu
* @date 2019/01/11.
*/
@Service
@Transactional
public class BusBaseInfoServiceImpl extends AbstractService<BusBaseInfo> implements BusBaseInfoService {
    @Resource
    private BusBaseInfoMapper busBaseInfoMapper;

    public List<BusBaseFullInfo> getBusBaseInfo(String busNumber,String rangeName,String busDriverAccount,String busMomAccount,String busSupplierName){
        return busBaseInfoMapper.getBusBaseInfo(busNumber,rangeName,busDriverAccount,busMomAccount,busSupplierName);
    }


}
