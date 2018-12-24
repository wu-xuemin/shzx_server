package com.eservice.api.service.impl;

import com.eservice.api.dao.StudentMapper;
import com.eservice.api.model.student.Student;
import com.eservice.api.service.StudentService;
import com.eservice.api.core.AbstractService;
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

    public List<Student> getStudentsByBusNumber(String busNumber) {
        return studentMapper.getStudentsByBusNumber(busNumber);
    }
}
