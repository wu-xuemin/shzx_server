package com.eservice.api.service.impl;

import com.eservice.api.dao.BusSupplierMapper;
import com.eservice.api.model.bus_supplier.BusSupplier;
import com.eservice.api.service.BusSupplierService;
import com.eservice.api.core.AbstractService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
* Class Description: xxx
* @author Wilson Hu
* @date 2018/12/17.
*/
@Service
@Transactional
public class BusSupplierServiceImpl extends AbstractService<BusSupplier> implements BusSupplierService {
    @Resource
    private BusSupplierMapper busSupplierMapper;

}
