package com.eservice.api.web;
import com.eservice.api.core.Result;
import com.eservice.api.core.ResultGenerator;
import com.eservice.api.model.booking_record.BookingRecord;
import com.eservice.api.service.BookingRecordService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
* Class Description: xxx
* @author Wilson Hu
* @date 2018/12/17.
*/
@RestController
@RequestMapping("/booking/record")
@Api(description = "学生的班车预约变更")
public class BookingRecordController {
    @Resource
    private BookingRecordService bookingRecordService;

    @PostMapping("/add")
    public Result add(BookingRecord bookingRecord) {
        bookingRecordService.save(bookingRecord);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/delete")
    public Result delete(@RequestParam Integer id) {
        bookingRecordService.deleteById(id);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/update")
    public Result update(BookingRecord bookingRecord) {
        bookingRecordService.update(bookingRecord);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/detail")
    public Result detail(@RequestParam Integer id) {
        BookingRecord bookingRecord = bookingRecordService.findById(id);
        return ResultGenerator.genSuccessResult(bookingRecord);
    }

    @PostMapping("/list")
    public Result list(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<BookingRecord> list = bookingRecordService.findAll();
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }
}
