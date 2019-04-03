package com.eservice.api.web;
import com.eservice.api.core.Result;
import com.eservice.api.core.ResultGenerator;
import com.eservice.api.model.banji.Banji;
import com.eservice.api.model.student.StudentInfo;
import com.eservice.api.service.common.Constant;
import com.eservice.api.service.common.SendSMS;
import com.eservice.api.service.impl.BanjiServiceImpl;
import com.eservice.api.service.impl.TransportRecordServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
* Class Description: xxx
* @author Wilson Hu
* @date 2018/12/17.
*/
@RestController
@RequestMapping("/banji")
@Api(description = "班级信息管理")
public class BanjiController {
    @Resource
    private BanjiServiceImpl banjiService;


    private final static Logger logger = LoggerFactory.getLogger(BanjiController.class);
    @PostMapping("/add")
    public Result add(Banji banji) {
        banji.setCreateTime(new Date());
        banjiService.save(banji);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/delete")
    public Result delete(@RequestParam Integer id) {
        banjiService.deleteById(id);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/update")
    public Result update(Banji banji) {
        banji.setUpdateTime(new Date());
        banjiService.update(banji);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/detail")
    public Result detail(@RequestParam Integer id) {
        Banji banji = banjiService.findById(id);
        return ResultGenerator.genSuccessResult(banji);
    }

    @PostMapping("/list")
    public Result list(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<Banji> list = banjiService.findAll();
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @ApiOperation("从xls excel里读取班级信息")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query",name = "fileName", value = "excel带路径文件名，比如C:\\Users\\wxm\\Desktop\\shzx_doc\\国际部学生基本信息20190126.xls") })
    @PostMapping("/parseInfoFromExcel")
    public Result parseInfoFromExcel(@RequestParam String fileName) {
        Result banji = banjiService.readFromExcel(fileName);
        return ResultGenerator.genSuccessResult(banji);
    }

    @PostMapping("/sendSMStest")
    public Result sendSMStest() {
        String str = banjiService.getAllAbsenceToday();
        //todo: SMS message
        return ResultGenerator.genSuccessResult(str);
    }
}
