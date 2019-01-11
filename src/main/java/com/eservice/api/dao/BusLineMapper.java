package com.eservice.api.dao;

import com.eservice.api.core.Mapper;
import com.eservice.api.model.bus_line.BusLine;
import com.eservice.api.model.bus_line.BusLineInfo;
import com.eservice.api.model.student.StudentInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BusLineMapper extends Mapper<BusLine> {

    List<BusLineInfo> getBusLinIfnoByBusMomAccount(@Param("busMomAccount")String busMomAccount);
    List<BusLineInfo> getBusesBySchoolPartition(@Param("schoolPartition")String schoolPartition);
    List<StudentInfo> getStudentsByBusNumber(@Param("busNumber")String busNumber);
}