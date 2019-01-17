package com.eservice.api.dao;

import com.eservice.api.core.Mapper;
import com.eservice.api.model.picked_students_info.PickedStudentsBusView;
import com.eservice.api.model.picked_students_info.PickedStudentsInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PickedStudentsInfoMapper extends Mapper<PickedStudentsInfo> {

    List<PickedStudentsBusView> selectStudentBus(@Param("busNumber")String busNumber,
                                                 @Param("busStation")String busStation,
                                                 @Param("gradeName")String gradeName,
                                                 @Param("className")String className,
                                                 @Param("queryStartTime")String queryStartTime,
                                                 @Param("queryFinishTime")String queryFinishTime
    );
}