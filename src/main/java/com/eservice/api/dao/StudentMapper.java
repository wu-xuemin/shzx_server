package com.eservice.api.dao;

import com.eservice.api.core.Mapper;
import com.eservice.api.model.student.Student;
import com.eservice.api.model.student.StudentInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StudentMapper extends Mapper<Student> {

    List<StudentInfo> getPlannedStudentsByBusNumberAndBusMode(@Param("busNumber") String busNumber,
                                                   @Param("busMode") String busMode);
}