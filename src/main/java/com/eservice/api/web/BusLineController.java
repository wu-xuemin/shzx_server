package com.eservice.api.web;
import com.eservice.api.core.Result;
import com.eservice.api.core.ResultGenerator;
import com.eservice.api.model.bus_line.BusLine;
import com.eservice.api.model.bus_line.BusLineInfo;
import com.eservice.api.model.student.StudentInfo;
import com.eservice.api.service.BusLineService;
import com.eservice.api.service.impl.BusLineServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
* Class Description: xxx
* @author Wilson Hu
* @date 2019/01/11.
*/
@RestController
@RequestMapping("/bus/line")
@Api(description = "校车线路信息")
public class BusLineController {
    @Resource
    private BusLineServiceImpl busLineService;

    @PostMapping("/add")
    public Result add(BusLine busLine) {
        busLineService.save(busLine);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/delete")
    public Result delete(@RequestParam Integer id) {
        busLineService.deleteById(id);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/update")
    public Result update(BusLine busLine) {
        busLineService.update(busLine);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/detail")
    public Result detail(@RequestParam Integer id) {
        BusLine busLine = busLineService.findById(id);
        return ResultGenerator.genSuccessResult(busLine);
    }

    @PostMapping("/list")
    public Result list(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<BusLine> list = busLineService.findAll();
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    /**
     * 根据巴士妈妈账号来获得巴士妈妈所在的巴士, 允许多条线都是同个bus妈妈，比如早午班
     * @param busMomAccount
     * @return
     */
    @ApiOperation("根据巴士妈妈账号来获得巴士妈妈所在的巴士班次信息,不同班次允许同个巴士妈妈")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "busMomAccount", value = "巴士妈妈账号，具有唯一性",required = true),
            @ApiImplicitParam(paramType = "query",name = "busMode", value = "校车班次，限于“早班”、“午班”两种，晚班不支持",required = true)})
    @PostMapping("/getBusLineInfoByBusMomAccountAndBusMode")
    public Result getBusLineInfoByBusMomAccountAndBusMode(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size,
                                        @RequestParam String busMomAccount,
                                        @RequestParam String busMode ) {
        PageHelper.startPage(page, size);
        List<BusLineInfo> list = busLineService.getBusLineInfoByBusMomAccountAndBusMode(busMomAccount,busMode);
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @ApiOperation("根据校区来获得该校区的所有巴士线路相关信息")
    @PostMapping("/getBusLineInfoBySchoolPartition")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query",name = "schoolPartition", value = "校区，目前只有“浦东”、“浦西” ")})
    public Result getBusLineInfoBySchoolPartition(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size,
                                          @RequestParam String schoolPartition) {
        PageHelper.startPage(page, size);
        List<BusLineInfo> list = busLineService.getBusLineInfoBySchoolPartition(schoolPartition);
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }


    @ApiOperation("根据校车编号 来获得该校车的所有学生")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "busNumber", value = "校车编号，比如 xc001"),
    })
    @PostMapping("/getStudentsByBusNumber")
    public Result getStudentsByBusNumber(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size,
                                         @RequestParam String busNumber ) {
        PageHelper.startPage(page, size);
        List<StudentInfo> list = busLineService.getStudentsByBusNumber(busNumber);
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }
}
