package com.eservice.api.dao;

import com.eservice.api.core.Mapper;
import com.eservice.api.model.student.Student;
import com.eservice.api.model.student.StudentInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StudentMapper extends Mapper<Student> {

    List<StudentInfo> getPlannedStudentsMorningByBusNumber(@Param("busNumber") String busNumber);

    List<StudentInfo> getPlannedStudentsAfternoonByBusNumber(@Param("busNumber") String busNumber);

    StudentInfo getSutdentInfo(@Param("studentNumber") String studentNumber);

    List<StudentInfo> getPlannedStudentsMorning(@Param("busNumber") String busNumber,@Param("busStation") String busStation);

    List<StudentInfo> getPlannedStudentsAfternoon(@Param("busNumber") String busNumber,@Param("busStation") String busStation);

    List<StudentInfo> getStudents(@Param("className") String className);
}