package com.eservice.api.dao;

import com.eservice.api.core.Mapper;
import com.eservice.api.model.bus_base_info.BusBaseFullInfo;
import com.eservice.api.model.bus_base_info.BusBaseInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BusBaseInfoMapper extends Mapper<BusBaseInfo> {
    List<BusBaseFullInfo> getBusBaseInfo(@Param("busNumber") String busNumber,
                                         @Param("rangeName") String rangeName,
                                         @Param("busDriverAccount") String busDriverAccount,
                                         @Param("busMomAccount") String busMomAccount,
                                         @Param("busSupplierName") String busSupplierName,
                                         @Param("schoolPartition") String schoolPartition,
                                         @Param("keyWord") String keyWord
    );
}