package com.eservice.api.service.impl;

import com.eservice.api.dao.NightPickedMapper;
import com.eservice.api.model.night_picked.NightPicked;
import com.eservice.api.service.NightPickedService;
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
public class NightPickedServiceImpl extends AbstractService<NightPicked> implements NightPickedService {
    @Resource
    private NightPickedMapper nightPickedMapper;

}
