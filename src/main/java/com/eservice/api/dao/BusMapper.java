package com.eservice.api.dao;

import com.eservice.api.core.Mapper;
import com.eservice.api.model.bus.Bus;
import com.eservice.api.model.student.Student;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BusMapper extends Mapper<Bus> {
    Bus getBusByBusMomAccount(@Param("busMomAccount")String busMomAccount);
    List<Bus> getBusesBySchoolPartition(@Param("schoolPartition")String schoolPartition);
    List<Student> getStudentsByBusNumber(@Param("busNumber")String busNumber);
}