package com.eservice.api.web;
import com.eservice.api.core.Result;
import com.eservice.api.core.ResultGenerator;
import com.eservice.api.model.transport_record.TransportRecord;
import com.eservice.api.service.TransportRecordService;
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
@RequestMapping("/transport/record")
@Api("接送记录管理")
public class TransportRecordController {
    @Resource
    private TransportRecordService transportRecordService;

    @PostMapping("/add")
    public Result add(TransportRecord transportRecord) {
        transportRecordService.save(transportRecord);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/delete")
    public Result delete(@RequestParam Integer id) {
        transportRecordService.deleteById(id);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/update")
    public Result update(TransportRecord transportRecord) {
        transportRecordService.update(transportRecord);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/detail")
    public Result detail(@RequestParam Integer id) {
        TransportRecord transportRecord = transportRecordService.findById(id);
        return ResultGenerator.genSuccessResult(transportRecord);
    }

    @PostMapping("/list")
    public Result list(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<TransportRecord> list = transportRecordService.findAll();
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }
}
