package com.eservice.api.dao;

import com.eservice.api.core.Mapper;
import com.eservice.api.model.transport_range.TransportRange;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TransportRangeMapper extends Mapper<TransportRange> {

      List<TransportRange> getTransportRangeByBusNumberAndBusMode(@Param("busNumber")String busNumber, @Param("busMode")String busMode);
}