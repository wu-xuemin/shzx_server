package com.eservice.api.dao;

import com.eservice.api.core.Mapper;
import com.eservice.api.model.bus_line.BusLine;
import com.eservice.api.model.bus_line.BusLineInfo;
import com.eservice.api.model.student.StudentInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface BusLineMapper extends Mapper<BusLine> {

    List<BusLineInfo> getBusLineInfoByBusMomAccount(@Param("busMomAccount")String busMomAccount);
    List<BusLineInfo> getBusLineInfoByBusDriverAccount(@Param("busDriverAccount")String busDriverAccount);
    List<BusLineInfo> getBusLineInfoBySchoolPartition(@Param("schoolPartition")String schoolPartition);
    List<StudentInfo> getStudents(@Param("busNumber")String busNumber,@Param("busMode")String busMode);
    BusLine getBusLineInfoByBusNumberAndBusMode( @Param("busNumber")String busNumber,@Param("busMode")String busMode);
    @Select("select * from bus_line where valid = 1 and ( mode = '${busMode}')")
    List<BusLine> getBusLinesByMode(@Param("busMode")String busMode);
    @Select("select * from bus_line where (`mode` = '上学' or `mode` = '放学' ) and valid = 1 and ( name like '%${queryKey}%' or stations like '%${queryKey}%') ORDER BY name +0")
    List<BusLine> list(@Param("queryKey")String queryKey);
    @Select("select * from bus_line bl left join bus_base_info bbi on bl.bus_base_info = bbi.id where bl.valid = 1 and bbi.number = '${busNumber}'")
    List<BusLine> getBusLineByBusNumber(@Param("busNumber")String busNumber);
    @Select("SELECT * from bus_line WHERE (`mode` = '上学' or `mode` = '放学' )ORDER BY `mode`,name +0 ")
    List<BusLine> listByName();
}