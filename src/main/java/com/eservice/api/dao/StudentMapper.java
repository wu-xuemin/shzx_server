package com.eservice.api.dao;

import com.eservice.api.core.Mapper;
import com.eservice.api.model.student.Student;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StudentMapper extends Mapper<Student> {
    List<Student> getStudentsByBusNumber(@Param("busNumber") String busNumber);
}