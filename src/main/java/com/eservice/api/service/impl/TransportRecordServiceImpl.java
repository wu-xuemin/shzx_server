package com.eservice.api.service.impl;

import com.eservice.api.dao.TransportRecordMapper;
import com.eservice.api.model.student.Student;
import com.eservice.api.model.student.StudentInfo;
import com.eservice.api.model.transport_record.TransportRecord;
import com.eservice.api.model.transport_record.TransportRecordInfo;
import com.eservice.api.service.TransportRecordService;
import com.eservice.api.core.AbstractService;
import com.eservice.api.service.common.Constant;
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
public class TransportRecordServiceImpl extends AbstractService<TransportRecord> implements TransportRecordService {
    @Resource
    private TransportRecordMapper transportRecordMapper;

    public List<TransportRecordInfo> selectTransportRecord(  String queryStartTime,
                                                             String queryFinishTime,
                                                             String studentName,
                                                             String busNumber,
                                                             String busMode,
                                                             String busStationName,
                                                             String grade,
                                                             String className) {
        return transportRecordMapper.selectTransportRecord(queryStartTime,
                                                            queryFinishTime,
                                                            studentName,
                                                            busNumber,
                                                            busMode,
                                                            busStationName,
                                                            grade,
                                                            className);
    }

     public List<Student> getUnplannedStudents(String busNumber,String busMode, String queryStartTime, String queryFinishTime){
        if(Constant.BUS_MODE_MORNING.equalsIgnoreCase(busMode)) {
            return transportRecordMapper.getUnplannedStudentsMorning(busNumber, queryStartTime, queryFinishTime);
        }else if(Constant.BUS_MODE_AFTERNOON.equalsIgnoreCase(busMode)){
            return transportRecordMapper.getUnplannedStudentsAfternoon(busNumber, queryStartTime, queryFinishTime);
        } else {
            return null;
        }
     }

}
