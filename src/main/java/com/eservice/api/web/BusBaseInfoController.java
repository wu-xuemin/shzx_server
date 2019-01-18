package com.eservice.api.web;

import com.eservice.api.core.Result;
import com.eservice.api.core.ResultGenerator;
import com.eservice.api.model.bus_base_info.BusBaseFullInfo;
import com.eservice.api.model.bus_base_info.BusBaseInfo;
import com.eservice.api.service.BusBaseInfoService;
import com.eservice.api.service.impl.BusBaseInfoServiceImpl;
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
 *
 * @author Wilson Hu
 * @date 2019/01/11.
 */
@RestController
@RequestMapping("/bus/base/info")
@Api(description = "校车基础信息")
public class BusBaseInfoController {
    @Resource
    private BusBaseInfoServiceImpl busBaseInfoService;

    @PostMapping("/add")
    public Result add(BusBaseInfo busBaseInfo) {
        busBaseInfoService.save(busBaseInfo);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/delete")
    public Result delete(@RequestParam Integer id) {
        busBaseInfoService.deleteById(id);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/update")
    public Result update(BusBaseInfo busBaseInfo) {
        busBaseInfoService.update(busBaseInfo);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/detail")
    public Result detail(@RequestParam Integer id) {
        BusBaseInfo busBaseInfo = busBaseInfoService.findById(id);
        return ResultGenerator.genSuccessResult(busBaseInfo);
    }

    @PostMapping("/list")
    public Result list(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<BusBaseInfo> list = busBaseInfoService.findAll();
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @ApiOperation("根据校车编号等 去查询校车详情包括巴士妈妈名字等")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "busNumber", value = " 校车编号"),
            @ApiImplicitParam(paramType = "query", name = "rangeName", value = " 区间名称"),
            @ApiImplicitParam(paramType = "query", name = "busDriverAccount", value = " 司机账号"),
            @ApiImplicitParam(paramType = "query", name = "busMomAccount", value = " 巴士妈妈账号"),
            @ApiImplicitParam(paramType = "query", name = "busSupplierName", value = " 供应商名称"),
            @ApiImplicitParam(paramType = "query", name = "keyWord", value = " 关键字")
    })
    @PostMapping("/getBusBaseInfo")
    public Result getBusBaseInfo(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size,
                                 String busNumber,
                                 String rangeName,
                                 String busDriverAccount,
                                 String busMomAccount,
                                 String busSupplierName,
                                 String schoolPartition,
                                 String keyWord
                                 ) {
        PageHelper.startPage(page, size);
        List<BusBaseFullInfo> list = busBaseInfoService.getBusBaseInfo(busNumber, rangeName, busDriverAccount, busMomAccount, busSupplierName,schoolPartition,keyWord);
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }


}
