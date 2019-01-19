package com.eservice.api.web;

import com.eservice.api.core.Result;
import com.eservice.api.core.ResultGenerator;
import com.eservice.api.model.picked_students_info.PickedStudentsBusView;
import com.eservice.api.model.picked_students_info.PickedStudentsInfo;
import com.eservice.api.model.student.Student;
import com.eservice.api.service.PickedStudentsInfoService;
import com.eservice.api.service.impl.PickedStudentsInfoServiceImpl;
import com.eservice.api.service.impl.StudentServiceImpl;
import com.eservice.api.service.impl.TransportRecordServiceImpl;
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
import java.util.Date;
import java.util.List;

/**
 * Class Description: xxx
 *
 * @author Wilson Hu
 * @date 2018/12/17.
 */
@RestController
@RequestMapping("/picked/students/info")
@Api(description = "已接到的学生的接送信息")
public class PickedStudentsInfoController {
    @Resource
    private PickedStudentsInfoServiceImpl pickedStudentsInfoService;
    @Resource
    private TransportRecordServiceImpl transportRecordService;
    @Resource
    private StudentServiceImpl studentService;

    @ApiOperation("增加一次学生乘车记录（比如每次刷脸成功时）")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "boardTime", value = " board_time参数留空，在后台会重置，以服务器时间为准"),
            @ApiImplicitParam(paramType = "query", name = "studentId", value = "这个studentId会被StudentNumber对应的studetnID替换，即以StudentNumber为准",required = false),
            @ApiImplicitParam(paramType = "query", name = "studentNumber", value = "学号",required = true)})
    @PostMapping("/add")
    public Result add(PickedStudentsInfo pickedStudentsInfo, String studentNumber) {
        if (pickedStudentsInfo == null) {
            return ResultGenerator.genFailResult("pickedStudentsInfo 不能为空");
        }
        if (transportRecordService.findById(pickedStudentsInfo.getTransportRecordId()) == null) {
            return ResultGenerator.genFailResult("请检查 pickedStudentsInfo的TransportRecordId参数，是否实际已创建对应的TransportRecord");
        }
        Student student = studentService.getSutdentInfo(studentNumber);
        if(student == null){
            return ResultGenerator.genFailResult("请检查studentNumber参数");
        }
        pickedStudentsInfo.setStudentId(student.getId());
        pickedStudentsInfo.setBoardTime(new Date());
        pickedStudentsInfoService.save(pickedStudentsInfo);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/delete")
    public Result delete(@RequestParam Integer id) {
        pickedStudentsInfoService.deleteById(id);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/update")
    public Result update(PickedStudentsInfo pickedStudentsInfo) {
        pickedStudentsInfoService.update(pickedStudentsInfo);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/detail")
    public Result detail(@RequestParam Integer id) {
        PickedStudentsInfo pickedStudentsInfo = pickedStudentsInfoService.findById(id);
        return ResultGenerator.genSuccessResult(pickedStudentsInfo);
    }

    @PostMapping("/list")
    public Result list(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<PickedStudentsInfo> list = pickedStudentsInfoService.findAll();
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @PostMapping("/selectStudentBus")
    public Result selectStudentBus(@RequestParam(defaultValue = "0") Integer page,
                                   @RequestParam(defaultValue = "0") Integer size,
                                   @RequestParam(defaultValue = "") String busNumber,
                                   @RequestParam(defaultValue = "") String busStation,
                                   @RequestParam(defaultValue = "") String gradeName,
                                   @RequestParam(defaultValue = "") String className,
                                   String queryStartTime,
                                   String queryFinishTime,
                                   String keyWord
    ) {
        PageHelper.startPage(page, size);
        List<PickedStudentsBusView> list = pickedStudentsInfoService.selectStudentBus(busNumber, busStation, gradeName, className, queryStartTime, queryFinishTime,keyWord);
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }
}
