//package com.eservice.api.web;
//import com.eservice.api.core.Result;
//import com.eservice.api.core.ResultGenerator;
//import com.eservice.api.model.afternoon_picked.AfternoonPicked;
//import com.eservice.api.service.AfternoonPickedService;
//import com.github.pagehelper.PageHelper;
//import com.github.pagehelper.PageInfo;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.annotation.Resource;
//import java.util.List;
//
///**
//* Class Description: xxx
//* @author Wilson Hu
//* @date 2019/02/13.
//*/
//@RestController
//@RequestMapping("/afternoon/picked")
//public class AfternoonPickedController {
//    @Resource
//    private AfternoonPickedService afternoonPickedService;
//
//    @PostMapping("/add")
//    public Result add(AfternoonPicked afternoonPicked) {
//        afternoonPickedService.save(afternoonPicked);
//        return ResultGenerator.genSuccessResult();
//    }
//
//    @PostMapping("/delete")
//    public Result delete(@RequestParam Integer id) {
//        afternoonPickedService.deleteById(id);
//        return ResultGenerator.genSuccessResult();
//    }
//
//    @PostMapping("/update")
//    public Result update(AfternoonPicked afternoonPicked) {
//        afternoonPickedService.update(afternoonPicked);
//        return ResultGenerator.genSuccessResult();
//    }
//
//    @PostMapping("/detail")
//    public Result detail(@RequestParam Integer id) {
//        AfternoonPicked afternoonPicked = afternoonPickedService.findById(id);
//        return ResultGenerator.genSuccessResult(afternoonPicked);
//    }
//
//    @PostMapping("/list")
//    public Result list(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
//        PageHelper.startPage(page, size);
//        List<AfternoonPicked> list = afternoonPickedService.findAll();
//        PageInfo pageInfo = new PageInfo(list);
//        return ResultGenerator.genSuccessResult(pageInfo);
//    }
//}
