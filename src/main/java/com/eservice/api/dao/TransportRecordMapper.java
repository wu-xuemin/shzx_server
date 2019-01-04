package com.eservice.api.dao;

import com.eservice.api.core.Mapper;
import com.eservice.api.model.transport_record.TransportRecord;
import com.eservice.api.model.transport_record.TransportRecordInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TransportRecordMapper extends Mapper<TransportRecord> {

    List<TransportRecordInfo> selectTransportRecord(@Param("queryStartTime") String queryStartTime,
                                                    @Param("queryFinishTime") String queryFinishTime,
                                                    @Param("studentName") String studentName,
                                                    @Param("busNumber") String busNumber,
                                                    @Param("busMode") String busMode,
                                                    @Param("busStationName") String busStationName,
                                                    @Param("grade") String grade,
                                                    @Param("className") String className );

}