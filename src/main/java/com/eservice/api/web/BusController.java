package com.eservice.api.web;
import com.eservice.api.core.Result;
import com.eservice.api.core.ResultGenerator;
import com.eservice.api.model.bus.Bus;
import com.eservice.api.model.student.Student;
import com.eservice.api.service.BusService;
import com.eservice.api.service.impl.BusServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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
public class BusController {
    @Resource
    private BusServiceImpl busService;

    @PostMapping("/add")
    public Result add(Bus bus) {
        busService.save(bus);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/delete")
    public Result delete(@RequestParam Integer id) {
        busService.deleteById(id);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/update")
    public Result update(Bus bus) {
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

    @ApiOperation("根据校区来获得该校区的所有巴士的信息")
    @PostMapping("/getBusesBySchoolPartition")
    public Result getBusesBySchoolPartition(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size,
                                          @RequestParam String schoolPartition) {
        PageHelper.startPage(page, size);
        List<Bus> list = busService.getBusesBySchoolPartition(schoolPartition);
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }


    @ApiOperation("根据校车编号来获得该校车的所有学生")
    @PostMapping("/getStudentsByBusNumber")
    public Result getStudentsByBusNumber(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size,
                                          @RequestParam String busNumber) {
        PageHelper.startPage(page, size);
        List<Student> list = busService.getStudentsByBusNumber(busNumber);
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

}
