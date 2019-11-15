package com.eservice.api.service.impl;

import com.eservice.api.dao.PickedStudentsInfoMapper;
import com.eservice.api.model.picked_students_info.PickedStudentsBusView;
import com.eservice.api.model.picked_students_info.PickedStudentsInfo;
import com.eservice.api.service.PickedStudentsInfoService;
import com.eservice.api.core.AbstractService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import javax.annotation.Resource;


/**
 * Class Description: xxx
 *
 * @author Wilson Hu
 * @date 2018/12/17.
 */
@Service
@Transactional
public class PickedStudentsInfoServiceImpl extends AbstractService<PickedStudentsInfo> implements PickedStudentsInfoService {
    @Resource
    private PickedStudentsInfoMapper pickedStudentsInfoMapper;

    public List<PickedStudentsBusView> selectStudentBus(String busNumber,
                                                        String busStation,
                                                        String gradeName,
                                                        String className,
                                                        String checkMode,
                                                        String modeName,
                                                        String flag,
                                                        String queryStartTime,
                                                        String queryFinishTime,
                                                        String keyWord
                                                        ) {
        return pickedStudentsInfoMapper.selectStudentBus(busNumber, busStation, gradeName, className,checkMode,modeName,flag,queryStartTime,queryFinishTime,keyWord);
    }

    public List<PickedStudentsInfo> getPickedStudentInfo(String studentNumber,Integer transportRecordId){
        return pickedStudentsInfoMapper.getPickedStudentInfo(studentNumber,transportRecordId);
    }

}
