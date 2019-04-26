package com.eservice.api.service.common;

import com.eservice.api.model.booking_record.BookingRecord;
import com.eservice.api.model.bus_line.BusLine;
import com.eservice.api.model.student.Student;
import com.eservice.api.service.BookingRecordService;
import com.eservice.api.service.StudentService;
import com.eservice.api.service.impl.BusLineServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class UpdateStudentTimer {

    private final static Logger logger= LoggerFactory.getLogger(UpdateStudentTimer.class);

    @Resource
    private BookingRecordService bookingRecordService;

    @Resource
    private StudentService studentService;

    @Resource
    private BusLineServiceImpl busLineService;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private List<BookingRecord> list = new ArrayList<>();


    @Scheduled(cron = "0 0 1 * * ?")
    public void fetchBookingRecord() {
        Date date=new Date();
        String yesterdayDate = sdf.format(date);
        logger.info(yesterdayDate);
        Condition condition = new Condition(BookingRecord.class);
        condition.createCriteria().andEqualTo("changeDate", yesterdayDate);
        List<BookingRecord> bookingRecords = bookingRecordService.findByCondition(condition);
        for (BookingRecord bookingRecord : bookingRecords) {
            if (bookingRecord.getConfirmStatus().equals("1")) {
                list.add(bookingRecord);
            }
        }
        Student stu = new Student();
        for (BookingRecord book : list) {
            List<BusLine> busLines = busLineService.getBusId(book.getNewBusLine().toString());
            stu.setId(book.getStudent());
            stu.setBusLineMorning(busLines.get(0).getId());
            stu.setBusLineAfternoon(busLines.get(1).getId());
            stu.setBoardStationMorning(book.getNewStation());
            stu.setBoardStationAfternoon(book.getNewStation());
            stu.setUpdateTime(new Date());
        }

        studentService.update(stu);
        logger.info("修改学生信息成功");
    }
}
