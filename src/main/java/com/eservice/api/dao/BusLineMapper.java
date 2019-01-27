package com.eservice.api.dao;

import com.eservice.api.core.Mapper;
import com.eservice.api.model.bus_line.BusLine;
import com.eservice.api.model.bus_line.BusLineInfo;
import com.eservice.api.model.student.StudentInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BusLineMapper extends Mapper<BusLine> {

    List<BusLineInfo> getBusLineInfoByBusMomAccountAndBusMode(@Param("busMomAccount")String busMomAccount,@Param("busMode")String busMode);
    List<BusLineInfo> getBusLineInfoBySchoolPartition(@Param("schoolPartition")String schoolPartition);
    List<StudentInfo> getStudents(@Param("busNumber")String busNumber,@Param("busMode")String busMode);
    BusLine getBusLineInfoByBusNumberAndBusMode( @Param("busNumber")String busNumber,@Param("busMode")String busMode);
}