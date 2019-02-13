package com.eservice.api.service.impl;

import com.eservice.api.dao.AfternoonPickedMapper;
import com.eservice.api.model.afternoon_picked.AfternoonPicked;
import com.eservice.api.service.AfternoonPickedService;
import com.eservice.api.core.AbstractService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
* Class Description: xxx
* @author Wilson Hu
* @date 2019/02/13.
*/
@Service
@Transactional
public class AfternoonPickedServiceImpl extends AbstractService<AfternoonPicked> implements AfternoonPickedService {
    @Resource
    private AfternoonPickedMapper afternoonPickedMapper;

}
