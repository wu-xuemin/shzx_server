package com.eservice.api.web;
import com.eservice.api.core.Result;
import com.eservice.api.core.ResultGenerator;
import com.eservice.api.model.student.Student;
import com.eservice.api.model.student.StudentInfo;
import com.eservice.api.service.StudentService;
import com.eservice.api.service.impl.StudentServiceImpl;
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
* @date 2018/12/24.
*/
@RestController
@RequestMapping("/student")
@Api(description = "学生信息管理")
public class StudentController {
    @Resource
    private StudentServiceImpl studentService;

    @PostMapping("/add")
    public Result add(Student student) {
        studentService.save(student);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/delete")
    public Result delete(@RequestParam Integer id) {
        studentService.deleteById(id);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/update")
    public Result update(Student student) {
        studentService.update(student);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/detail")
    public Result detail(@RequestParam Integer id) {
        Student student = studentService.findById(id);
        return ResultGenerator.genSuccessResult(student);
    }

    @PostMapping("/list")
    public Result list(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<Student> list = studentService.findAll();
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @ApiOperation("根据校车编号和模式（班次），返回该校车班次的计划乘坐的学生")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "busNumber", value = "校车编号，比如 xc001",required = true),
            @ApiImplicitParam(paramType = "query",name = "busMode", value = "校车班次，内容限于“早班”、“午班”两种，晚班没有固定乘坐计划所以不支持",required = true)
    })
    @PostMapping("/getPlannedStudentsByBusNumberAndBusMode")
    public Result getPlannedStudentsByBusNumberAndBusMode(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size,
                                                    @RequestParam() String busNumber,
                                                    @RequestParam() String busMode) {
        PageHelper.startPage(page, size);
        List<StudentInfo> list = studentService.getPlannedStudentsByBusNumberAndBusMode(busNumber, busMode);
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @ApiOperation("根据学号查找某学生详细信息")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query",name = "studentNumber", value = "学生的学号") })
    @PostMapping("/getSutdentInfo")
    public Result getSutdentInfo(@RequestParam String studentNumber) {
        Student student = studentService.getSutdentInfo(studentNumber);
        return ResultGenerator.genSuccessResult(student);
    }

    @ApiOperation("根据班级名称获取学生列表")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query",name = "className", value = "班级名称") })
    @PostMapping("/getStudents")
    public Result getStudents(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size,@RequestParam String className) {

        PageHelper.startPage(page, size);
        List<StudentInfo> students = studentService.getStudents(className);
        PageInfo pageInfo = new PageInfo(students);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @ApiOperation("根据校车编号+模式（早班午班）+站点 查找计划乘坐的学生列表,不传参数则不限制对应的条件")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "busNumber", value = "校车编号" ),
            @ApiImplicitParam(paramType = "query",name = "busMode", value = "校车班次，内容限于“早班”、“午班”两种，早班午班每个学生都是同辆车，这个参数传不传都一样"),
            @ApiImplicitParam(paramType = "query",name = "busStation", value = "校车站点名称，比如1号路口" )})
    @PostMapping("/getPlannedStudents")
    public Result getPlannedStudents(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size,
                                     String busNumber,
                                     String busMode,
                                     String busStation) {
        PageHelper.startPage(page, size);
        List<StudentInfo> list = studentService.getPlannedStudents(busNumber, busMode,busStation);
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }
}
