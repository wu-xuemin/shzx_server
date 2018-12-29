package com.eservice.api.web;
import com.eservice.api.core.Result;
import com.eservice.api.core.ResultGenerator;
import com.eservice.api.model.student.Student;
import com.eservice.api.service.StudentService;
import com.eservice.api.service.impl.StudentServiceImpl;
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
* @date 2018/12/24.
*/
@RestController
@RequestMapping("/student")
@Api("学生信息管理")
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

    /**
     * 根据校车编号，返回该校车的学生
     * @param page
     * @param size
     * @param busNumber
     * @return
     */
    @PostMapping("/getStudentsByBusNumber")
    public Result getStudentsByBusNumber(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size,
                                        @RequestParam(defaultValue = "0") String busNumber) {
        PageHelper.startPage(page, size);
        List<Student> list = studentService.getStudentsByBusNumber(busNumber);
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

}
