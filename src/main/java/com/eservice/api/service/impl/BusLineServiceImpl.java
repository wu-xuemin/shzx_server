package com.eservice.api.service.impl;

import com.eservice.api.dao.BusLineMapper;
import com.eservice.api.model.bus_line.BusLine;
import com.eservice.api.model.bus_line.BusLineInfo;
import com.eservice.api.model.student.StudentInfo;
import com.eservice.api.service.BusLineService;
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
public class BusLineServiceImpl extends AbstractService<BusLine> implements BusLineService {
    @Resource
    private BusLineMapper busLineMapper;

    public List<BusLineInfo> getBusLinIfnoByBusMomAccount(String busMomAccount){
        return busLineMapper.getBusLinIfnoByBusMomAccount(busMomAccount);
    }
    public List<BusLineInfo> getBusesBySchoolPartition(String schoolPartition){
        return busLineMapper.getBusesBySchoolPartition(schoolPartition);
    }

    public List<StudentInfo> getStudentsByBusNumber(String busNumber){
        return busLineMapper.getStudentsByBusNumber(busNumber);
    }
}
