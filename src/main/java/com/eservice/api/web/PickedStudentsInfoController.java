package com.eservice.api.web;
import com.eservice.api.core.Result;
import com.eservice.api.core.ResultGenerator;
import com.eservice.api.model.picked_students_info.PickedStudentsInfo;
import com.eservice.api.service.PickedStudentsInfoService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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
@RequestMapping("/picked/students/info")
public class PickedStudentsInfoController {
    @Resource
    private PickedStudentsInfoService pickedStudentsInfoService;

    @PostMapping("/add")
    public Result add(PickedStudentsInfo pickedStudentsInfo) {
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
}
