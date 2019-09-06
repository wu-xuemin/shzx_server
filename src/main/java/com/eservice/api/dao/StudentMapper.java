package com.eservice.api.dao;

import com.eservice.api.core.Mapper;
import com.eservice.api.model.student.Student;
import com.eservice.api.model.student.StudentBusInfo;
import com.eservice.api.model.student.StudentInfo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface StudentMapper extends Mapper<Student> {

    List<StudentInfo> getPlannedStudentsMorningByBusNumber(@Param("busNumber") String busNumber);

    List<StudentInfo> getPlannedStudentsAfternoonByBusNumber(@Param("busNumber") String busNumber);

    StudentInfo getStudentInfo(@Param("studentNumber") String studentNumber);

    List<StudentInfo> getPlannedStudentsMorning(@Param("busNumber") String busNumber, @Param("busStation") String busStation,
                                                @Param("gradeName") String gradeName, @Param("className") String className);

    List<StudentInfo> getPlannedStudentsAfternoon(@Param("busNumber") String busNumber, @Param("busStation") String busStation,
                                                  @Param("gradeName") String gradeName, @Param("className") String className);

    List<StudentInfo> getPlannedStudents(@Param("busNumber") String busNumber, @Param("busStation") String busStation,
                                         @Param("gradeName") String gradeName, @Param("className") String className);

    List<StudentInfo> getStudents(@Param("gradeName") String gradeName, @Param("className") String className, @Param("queryKey") String queryKey);

    List<StudentInfo> getPlannedClassStudents(@Param("gradeName") String gradeName, @Param("className") String className, @Param("queryKey") String queryKey);

    @Delete("DELETE   from student where ( student.board_station_afternoon is NULL and student.board_station_morning is NULL)")
    Integer deleteStudentsNotRideSchoolBus();

    @Select("  SELECT * from student st where st.head_img is null ")
    List<Student> checkStudentImg();

    @Select("  SELECT * from student st where" +
            " (st.board_station_afternoon is null " +
            "or st.board_station_morning is null " +
            "or st.bus_line_afternoon is null " +
            "or st.bus_line_morning is null ) ")
    List<Student>checkStudentBus();
}