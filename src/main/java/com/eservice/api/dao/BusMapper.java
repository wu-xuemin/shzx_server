package com.eservice.api.dao;

import com.eservice.api.core.Mapper;
import com.eservice.api.model.bus.Bus;
import com.eservice.api.model.bus.BusInfo;
import com.eservice.api.model.student.StudentInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BusMapper extends Mapper<Bus> {
    Bus getBusByBusMomAccount(@Param("busMomAccount")String busMomAccount);
    List<BusInfo> getBusesBySchoolPartition(@Param("schoolPartition")String schoolPartition);
    List<StudentInfo> getStudentsByBusNumber(@Param("busNumber")String busNumber);
}