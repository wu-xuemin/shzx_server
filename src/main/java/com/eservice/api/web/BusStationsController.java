package com.eservice.api.web;
import com.eservice.api.core.Result;
import com.eservice.api.core.ResultGenerator;
import com.eservice.api.model.bus_stations.BusStations;
import com.eservice.api.service.BusStationsService;
import com.eservice.api.service.impl.BusStationsServiceImpl;
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
@RequestMapping("/bus/stations")
@Api(description = "校车站点管理")
public class BusStationsController {
    @Resource
    private BusStationsServiceImpl busStationsService;

    @PostMapping("/add")
    public Result add(BusStations busStations) {
        busStationsService.save(busStations);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/delete")
    public Result delete(@RequestParam Integer id) {
        busStationsService.deleteById(id);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/update")
    public Result update(BusStations busStations) {
        busStationsService.update(busStations);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/detail")
    public Result detail(@RequestParam Integer id) {
        BusStations busStations = busStationsService.findById(id);
        return ResultGenerator.genSuccessResult(busStations);
    }

    @PostMapping("/list")
    public Result list(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<BusStations> list = busStationsService.findAll();
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @ApiOperation("根据站点名称 查找站点信息")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query",name = "stationName", value = "站点名称",required = true) })
    @PostMapping("/getBusStation")
    public Result getBusStation(@RequestParam String stationName) {
        BusStations theBusStation = busStationsService.getBusStation(stationName);
        return ResultGenerator.genSuccessResult(theBusStation);
    }

    @ApiOperation("从xls excel里读取站点信息")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query",name = "fileName", value = "excel带路径文件名，比如C:\\Users\\wxm\\Desktop\\shzx_doc\\国际部学生基本信息20190126.xls") })
    @PostMapping("/parseInfoFromExcel")
    public Result parseInfoFromExcel(@RequestParam String fileName) {
        Result banji = busStationsService.readFromExcel(fileName);
        return ResultGenerator.genSuccessResult(banji);
    }

}
