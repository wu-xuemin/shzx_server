package com.eservice.api.web;
import com.eservice.api.core.Result;
import com.eservice.api.core.ResultGenerator;
import com.eservice.api.model.transport_record.TransportRecord;
import com.eservice.api.model.transport_record.TransportRecordInfo;
import com.eservice.api.service.impl.TransportRecordServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
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
    private TransportRecordServiceImpl transportRecordService;

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

    @ApiOperation("根据日期+查询条件(学生姓名)+校车+站点+年级+班级 查询乘车记录信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "queryStartTime", value = "查询的起始时间，比如 2018-12-19 10:16:57"),
            @ApiImplicitParam(paramType = "query",name = "queryFinishTime", value = "查询的结束时间，比如 2018-12-22 10:16:57"),
            @ApiImplicitParam(paramType = "query",name = "studentName", value = "查询的学生姓名（模糊查询），比如 小明"),
            @ApiImplicitParam(paramType = "query",name = "busNumber", value = "查询的校车编号，比如 XC001"),
            @ApiImplicitParam(paramType = "query",name = "busStationName", value = "查询的校车站点名称（包括了上车、下车），比如 11路口"),
            @ApiImplicitParam(paramType = "query",name = "grade", value = "查询的年级，比如 1，比如2"),
            @ApiImplicitParam(paramType = "query",name = "className", value = "查询的班级名称，比如1年级2班，比如 2年级2班")
    })
    @PostMapping("/selectTransportRecord")
    public Result selectTransportRecord(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size,
                                        String queryStartTime,
                                        String queryFinishTime,
                                        String studentName,
                                        String busNumber,
                                        String busStationName,
                                        String grade,
                                        String className) {
        PageHelper.startPage(page, size);
        List<TransportRecordInfo> list = transportRecordService.selectTransportRecord( queryStartTime,
                                                                                       queryFinishTime,
                                                                                       studentName,
                                                                                       busNumber,
                                                                                       busStationName,
                                                                                       grade,
                                                                                       className);
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

}
