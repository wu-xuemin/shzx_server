package com.eservice.api.service.impl;

import com.eservice.api.dao.NightLineMapper;
import com.eservice.api.model.night_line.NightLine;
import com.eservice.api.service.NightLineService;
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
public class NightLineServiceImpl extends AbstractService<NightLine> implements NightLineService {
    @Resource
    private NightLineMapper nightLineMapper;

}
