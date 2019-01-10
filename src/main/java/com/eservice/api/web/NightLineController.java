package com.eservice.api.web;
import com.eservice.api.core.Result;
import com.eservice.api.core.ResultGenerator;
import com.eservice.api.model.night_line.NightLine;
import com.eservice.api.service.NightLineService;
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
* @date 2018/12/17.
*/
@RestController
@RequestMapping("/night/line")
@Api(description = "晚班线路信息管理")
public class NightLineController {
    @Resource
    private NightLineService nightLineService;

    @PostMapping("/add")
    public Result add(NightLine nightLine) {
        nightLineService.save(nightLine);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/delete")
    public Result delete(@RequestParam Integer id) {
        nightLineService.deleteById(id);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/update")
    public Result update(NightLine nightLine) {
        nightLineService.update(nightLine);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/detail")
    public Result detail(@RequestParam Integer id) {
        NightLine nightLine = nightLineService.findById(id);
        return ResultGenerator.genSuccessResult(nightLine);
    }

    @PostMapping("/list")
    public Result list(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<NightLine> list = nightLineService.findAll();
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }
}
