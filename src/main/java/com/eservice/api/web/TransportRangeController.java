package com.eservice.api.web;
import com.eservice.api.core.Result;
import com.eservice.api.core.ResultGenerator;
import com.eservice.api.model.transport_range.TransportRange;
import com.eservice.api.service.TransportRangeService;
import com.eservice.api.service.impl.TransportRangeServiceImpl;
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
@RequestMapping("/transport/range")
@Api(description = "校车区间管理")
public class TransportRangeController {
    @Resource
    private TransportRangeServiceImpl transportRangeService;

    @PostMapping("/add")
    public Result add(TransportRange transportRange) {
        transportRangeService.save(transportRange);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/delete")
    public Result delete(@RequestParam Integer id) {
        transportRangeService.deleteById(id);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/update")
    public Result update(TransportRange transportRange) {
        transportRangeService.update(transportRange);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/detail")
    public Result detail(@RequestParam Integer id) {
        TransportRange transportRange = transportRangeService.findById(id);
        return ResultGenerator.genSuccessResult(transportRange);
    }

    @PostMapping("/list")
    public Result list(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<TransportRange> list = transportRangeService.findAll();
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @ApiOperation("根据校车编号+班次 去查询站点列表（包含在transport_range信息中）")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "busNumber", value = " 校车编号"),
            @ApiImplicitParam(paramType = "query",name = "busMode", value = " 班次，限于 “早班”、“午班”")})
    @PostMapping("/getTransportRangeByBusNumberAndBusMode")
    public Result getTransportRangeByBusNumberAndBusMode(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size,
                                               @RequestParam String busNumber,
                                               @RequestParam String busMode) {
        PageHelper.startPage(page, size);
        List<TransportRange> list = transportRangeService.getTransportRangeByBusNumberAndBusMode(busNumber,busMode);
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }
}
