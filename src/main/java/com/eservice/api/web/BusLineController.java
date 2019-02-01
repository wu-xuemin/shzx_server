package com.eservice.api.web;
import com.eservice.api.core.Result;
import com.eservice.api.core.ResultGenerator;
import com.eservice.api.model.bus_base_info.BusBaseInfo;
import com.eservice.api.model.bus_line.BusLine;
import com.eservice.api.model.bus_line.BusLineExcelHelper;
import com.eservice.api.model.bus_line.BusLineInfo;
import com.eservice.api.model.student.Student;
import com.eservice.api.model.student.StudentInfo;
import com.eservice.api.service.BusLineService;
import com.eservice.api.service.common.CommonService;
import com.eservice.api.service.impl.BusBaseInfoServiceImpl;
import com.eservice.api.service.impl.BusLineServiceImpl;
import com.eservice.api.service.impl.StudentServiceImpl;
import com.github.pagehelper.Constant;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    @Resource
    private BusBaseInfoServiceImpl busBaseInfoService;
    @Resource
    private StudentServiceImpl studentService;

    private final Logger logger = LoggerFactory.getLogger(BusLineController.class);

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
    @ApiOperation("根据巴士妈妈账号和班次来获得巴士线路等信息,不同班次允许同个巴士妈妈")
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
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query",name = "schoolPartition", value = "校区，目前只有“浦东”、“浦西” ",required = true)})
    public Result getBusLineInfoBySchoolPartition(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size,
                                          @RequestParam String schoolPartition) {
        PageHelper.startPage(page, size);
        List<BusLineInfo> list = busLineService.getBusLineInfoBySchoolPartition(schoolPartition);
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @ApiOperation("根据校车编号/早午班 来获得该校车/早午班的所有学生")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "busNumber", value = "校车编号，比如 xc001",required = true),
            @ApiImplicitParam(paramType = "query",name = "busMode", value = " 早班 午班,不填则不限制")
    })
    @PostMapping("/getStudents")
    public Result getStudents(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size,
                              @RequestParam String busNumber,
                              String busMode ) {
        PageHelper.startPage(page, size);
        List<StudentInfo> list = busLineService.getStudents(busNumber,busMode);
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @ApiOperation("根据校车编号/早午班 来获得该校车的线路信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "busNumber", value = "校车编号，比如 xc001",required = true),
            @ApiImplicitParam(paramType = "query",name = "busMode", value = " 早班 午班")
    })
    @PostMapping("/getBusLineInfoByBusNumberAndBusMode")
    public Result getBusLineInfoByBusNumberAndBusMode(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size,
                                                      @RequestParam String busNumber,
                                                      @RequestParam String busMode ) {

        BusLine busLine = busLineService.getBusLineInfoByBusNumberAndBusMode(busNumber,busMode);
        return ResultGenerator.genSuccessResult(busLine);
    }

    @ApiOperation("从xls excel里读取线路信息")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query",name = "fileName", value = "excel带路径文件名，比如C:\\Users\\wxm\\Desktop\\shzx_doc\\xxxxx.xls") })
    @PostMapping("/parseInfoFromExcel")
    public Result parseInfoFromExcel(@RequestParam String fileName) {
        Result banji = busLineService.readFromExcel(fileName);
        return ResultGenerator.genSuccessResult(banji);
    }

}
