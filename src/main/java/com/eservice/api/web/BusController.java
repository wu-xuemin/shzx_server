package com.eservice.api.web;
import com.eservice.api.core.Result;
import com.eservice.api.core.ResultGenerator;
import com.eservice.api.model.bus.Bus;
import com.eservice.api.model.bus.BusInfo;
import com.eservice.api.model.student.StudentInfo;
import com.eservice.api.service.common.Constant;
import com.eservice.api.service.impl.BusServiceImpl;
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
* @date 2018/12/17.
*/
@RestController
@RequestMapping("/bus")
@Api(description = "校车信息管理")
public class BusController {
    @Resource
    private BusServiceImpl busService;

    @PostMapping("/add")
    @ApiOperation("增加校车， 校车班次(mode)，限于 \"上午接送\"、\"下午接送两种\" ")
    public Result add(Bus bus) {
        if(bus.getMode().equals(Constant.BUS_MODE_MORNING) || bus.getMode().equals(Constant.BUS_MODE_AFTERNOON)){
        } else {
            return ResultGenerator.genFailResult("busMode 只能是 " +  Constant.BUS_MODE_MORNING + " / " + Constant.BUS_MODE_AFTERNOON );
        }
        busService.save(bus);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/delete")
    public Result delete(@RequestParam Integer id) {
        busService.deleteById(id);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/update")
    @ApiOperation("更新校车信息， 校车班次(mode)，限于 \"上午接送\"、\"下午接送两种\" ")
    public Result update(Bus bus) {
        if(bus.getMode().equals(Constant.BUS_MODE_MORNING) || bus.getMode().equals(Constant.BUS_MODE_AFTERNOON)){
        } else {
            return ResultGenerator.genFailResult("busMode 只能是 " +  Constant.BUS_MODE_MORNING + " / " + Constant.BUS_MODE_AFTERNOON );
        }
        busService.update(bus);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/detail")
    public Result detail(@RequestParam Integer id) {
        Bus bus = busService.findById(id);
        return ResultGenerator.genSuccessResult(bus);
    }

    @PostMapping("/list")
    public Result list(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<Bus> list = busService.findAll();
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    /**
     * 根据巴士妈妈账号来获得巴士妈妈所在的巴士
     * @param busMomAccount
     * @return
     */

    @ApiOperation("根据巴士妈妈账号来获得巴士妈妈所在的巴士")
    @PostMapping("/getBusByBusMomAccount")
    public Result getBusByBusMomAccount(@RequestParam String busMomAccount) {
        Bus bus = busService.getBusByBusMomAccount(busMomAccount);
        return ResultGenerator.genSuccessResult(bus);
    }


    /**
     * 根据校区来获得该校区的所有巴士的信息
     * @param schoolPartition
     * @return
     */

    @ApiOperation("根据校区来获得该校区的所有巴士的相关信息")
    @PostMapping("/getBusesBySchoolPartition")
    public Result getBusesBySchoolPartition(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size,
                                          @RequestParam String schoolPartition) {
        PageHelper.startPage(page, size);
        List<BusInfo> list = busService.getBusesBySchoolPartition(schoolPartition);
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
        List<StudentInfo> list = busService.getStudentsByBusNumber(busNumber);
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

}
