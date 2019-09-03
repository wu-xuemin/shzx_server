package com.eservice.api.dao;

import com.eservice.api.core.Mapper;
import com.eservice.api.model.student.Student;
import com.eservice.api.model.student.StudentBusInfo;
import com.eservice.api.model.student.StudentInfo;
import com.eservice.api.model.transport_record.TransportRecord;
import com.eservice.api.model.transport_record.TransportRecordInfo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.access.method.P;

import java.util.List;

public interface TransportRecordMapper extends Mapper<TransportRecord> {

    List<TransportRecordInfo> selectTransportRecord(@Param("queryStartTime") String queryStartTime,
                                                    @Param("queryFinishTime") String queryFinishTime,
                                                    @Param("studentName") String studentName,
                                                    @Param("studentNumber") String studentNumber,
                                                    @Param("busNumber") String busNumber,
                                                    @Param("busMode") String busMode,
                                                    @Param("busStationName") String busStationName,
                                                    @Param("grade") String grade,
                                                    @Param("className") String className ,
                                                    @Param("recordFlag") String recordFlag,
                                                    @Param("recordStatus") String recordStatus);

    List<Student> getUnplannedStudentsMorning(@Param("busNumber") String busNumber,
                                       @Param("queryStartTime") String queryStartTime,
                                       @Param("queryFinishTime") String queryFinishTime);

    List<Student> getUnplannedStudentsAfternoon(@Param("busNumber") String busNumber,
                                              @Param("queryStartTime") String queryStartTime,
                                              @Param("queryFinishTime") String queryFinishTime);

    List<TransportRecord> getTransportRecord(@Param("busNumber") String busNumber,
                                             @Param("busLineName") String busLineName,
                                             @Param("busMode") String busMode,
                                             @Param("queryDate") String queryDate);

    void saveAndGetID(TransportRecord transportRecord);

    List<StudentInfo> getStudentsByTransportRecordId(@Param("TransportRecordId") String TransportRecordId);

    List<TransportRecordInfo> selectRecordStudent( @Param("gradeName") String grade,
                                              @Param("className") String className ,
                                              @Param("queryKey")String queryKey,
                                              @Param("queryStartTime") String queryStartTime,
                                              @Param("queryFinishTime")String queryFinishTime);
    @Delete("DELETE  from picked_students_info \n" +
            "where picked_students_info.transport_record_id in \n" +
            "(SELECT tr.id FROM transport_record tr JOIN bus_line bl on tr.bus_line = bl.id  WHERE tr.bus_number_in_tr = #{busLineNum})\n" +
            " and picked_students_info.board_time like  CONCAT('%', '${today}', '%') ")
    int clearTodaysPSIDataOfTheBus(@Param("busLineNum") String busLineNum, @Param("today") String today);

    @Delete( "DELETE  FROM transport_record WHERE  bus_number_in_tr = #{busLineNum} and begin_time like  CONCAT('%', '${today}', '%') ")
    int clearTodaysTRDataOfTheBus(@Param("busLineNum") String busLineNum, @Param("today") String today);
}