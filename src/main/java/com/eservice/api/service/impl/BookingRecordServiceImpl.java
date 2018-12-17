package com.eservice.api.service.impl;

import com.eservice.api.dao.BookingRecordMapper;
import com.eservice.api.model.booking_record.BookingRecord;
import com.eservice.api.service.BookingRecordService;
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
public class BookingRecordServiceImpl extends AbstractService<BookingRecord> implements BookingRecordService {
    @Resource
    private BookingRecordMapper bookingRecordMapper;

}
