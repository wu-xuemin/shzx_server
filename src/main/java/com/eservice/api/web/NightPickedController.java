//package com.eservice.api.web;
//import com.eservice.api.core.Result;
//import com.eservice.api.core.ResultGenerator;
//import com.eservice.api.model.night_picked.NightPicked;
//import com.eservice.api.service.NightPickedService;
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
//@RequestMapping("/night/picked")
//public class NightPickedController {
//    @Resource
//    private NightPickedService nightPickedService;
//
//    @PostMapping("/add")
//    public Result add(NightPicked nightPicked) {
//        nightPickedService.save(nightPicked);
//        return ResultGenerator.genSuccessResult();
//    }
//
//    @PostMapping("/delete")
//    public Result delete(@RequestParam Integer id) {
//        nightPickedService.deleteById(id);
//        return ResultGenerator.genSuccessResult();
//    }
//
//    @PostMapping("/update")
//    public Result update(NightPicked nightPicked) {
//        nightPickedService.update(nightPicked);
//        return ResultGenerator.genSuccessResult();
//    }
//
//    @PostMapping("/detail")
//    public Result detail(@RequestParam Integer id) {
//        NightPicked nightPicked = nightPickedService.findById(id);
//        return ResultGenerator.genSuccessResult(nightPicked);
//    }
//
//    @PostMapping("/list")
//    public Result list(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
//        PageHelper.startPage(page, size);
//        List<NightPicked> list = nightPickedService.findAll();
//        PageInfo pageInfo = new PageInfo(list);
//        return ResultGenerator.genSuccessResult(pageInfo);
//    }
//}
