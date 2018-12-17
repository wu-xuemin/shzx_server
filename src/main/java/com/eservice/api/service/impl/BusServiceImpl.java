package com.eservice.api.service.impl;

import com.eservice.api.dao.BusMapper;
import com.eservice.api.model.bus.Bus;
import com.eservice.api.service.BusService;
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
public class BusServiceImpl extends AbstractService<Bus> implements BusService {
    @Resource
    private BusMapper busMapper;

}
