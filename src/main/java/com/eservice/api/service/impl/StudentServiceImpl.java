package com.eservice.api.service.impl;

import com.eservice.api.dao.StudentMapper;
import com.eservice.api.model.student.Student;
import com.eservice.api.model.student.StudentInfo;
import com.eservice.api.service.StudentService;
import com.eservice.api.core.AbstractService;
import com.eservice.api.service.common.Constant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;


/**
* Class Description: xxx
* @author Wilson Hu
* @date 2018/12/24.
*/
@Service
@Transactional
public class StudentServiceImpl extends AbstractService<Student> implements StudentService {
    @Resource
    private StudentMapper studentMapper;

    /**
     * 这个函数可以用后面的 getPlannedStudents 替代。
     */
    public List<StudentInfo> getPlannedStudentsByBusNumberAndBusMode(String busNumber, String busMode) {
        if(busMode.equals(Constant.BUS_MODE_MORNING)){
            return studentMapper.getPlannedStudentsMorningByBusNumber(busNumber);
        } else if(busMode.equals(Constant.BUS_MODE_AFTERNOON)){
            return studentMapper.getPlannedStudentsAfternoonByBusNumber(busNumber);
        } else {
            return null;
        }
    }

    public StudentInfo getSutdentInfo(String studentNumber){
        return studentMapper.getSutdentInfo(studentNumber);
    }

    /**
     * @param busNumber
     * @param busMode 早班午班每个学生都是同辆车，这个参数传不传都一样, 都返回相同的学生
     * @param busStation
     */
    public List<StudentInfo> getPlannedStudents(String busNumber, String busMode,String busStation){
        if(busMode == null){
            return studentMapper.getPlannedStudents(busNumber,busStation);
        } else if(busMode.equals(Constant.BUS_MODE_MORNING)){
            return studentMapper.getPlannedStudentsMorning(busNumber,busStation);
        } else if(busMode.equals(Constant.BUS_MODE_AFTERNOON)){
            return studentMapper.getPlannedStudentsAfternoon(busNumber,busStation);
        } else {
            return null;
        }

    }

    public List<StudentInfo> getStudents(String className) {
        return studentMapper.getStudents(className);
    }}
