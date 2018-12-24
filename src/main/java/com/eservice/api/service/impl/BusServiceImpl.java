package com.eservice.api.service.impl;

import com.eservice.api.dao.BusMapper;
import com.eservice.api.model.bus.Bus;
import com.eservice.api.model.student.StudentInfo;
import com.eservice.api.service.BusService;
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
public class BusServiceImpl extends AbstractService<Bus> implements BusService {
    @Resource
    private BusMapper busMapper;

    public Bus getBusByBusMomAccount(String busMomAccount){
        return busMapper.getBusByBusMomAccount(busMomAccount);
    }
    public List<Bus> getBusesBySchoolPartition(String schoolPartition){
        return busMapper.getBusesBySchoolPartition(schoolPartition);
    }

    public List<StudentInfo> getStudentsByBusNumber(String busNumber){
        return busMapper.getStudentsByBusNumber(busNumber);
    }
}
