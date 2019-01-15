package com.eservice.api.service.impl;

import com.eservice.api.dao.TransportRangeMapper;
import com.eservice.api.model.transport_range.TransportRange;
import com.eservice.api.service.TransportRangeService;
import com.eservice.api.core.AbstractService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;


/**
* Class Description: xxx
* @author Wilson Hu
* @date 2018/12/17.
*/
@Service
@Transactional
public class TransportRangeServiceImpl extends AbstractService<TransportRange> implements TransportRangeService {
    @Resource
    private TransportRangeMapper transportRangeMapper;

    public List<TransportRange> getTransportRangeByBusNumberAndBusMode(String busNumber,String busMode){
     return transportRangeMapper.getTransportRangeByBusNumberAndBusMode(busNumber,busMode);
    }

}
