package com.eservice.api.dao;

import com.eservice.api.core.Mapper;
import com.eservice.api.model.booking_record.BookingRecord;
import com.eservice.api.model.booking_record.BookingRecordInfo;

import java.util.List;

public interface BookingRecordMapper extends Mapper<BookingRecord> {

    List<BookingRecordInfo> getBookingRecordInfo();
}