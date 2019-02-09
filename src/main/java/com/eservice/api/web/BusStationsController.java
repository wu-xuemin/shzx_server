package com.eservice.api.web;
import com.alibaba.fastjson.JSON;
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
    public Result add(String busStation) {
        BusStations busStationsObj = JSON.parseObject(busStation,BusStations.class);
        busStationsService.save(busStationsObj);
        return ResultGenerator.genSuccessResult();
    }

    /**
     *删除只设置valid值为0
     */
    @PostMapping("/delete")
    public Result delete(@RequestParam String busStation) {
        if(busStation != null){
            BusStations busStationObj = JSON.parseObject(busStation,BusStations.class);
            busStationObj.setValid(0);
            busStationsService.update(busStationObj);
        } else {
            ResultGenerator.genFailResult("参数不能为空！");
        }
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/update")
    public Result update(String busStation) {
        BusStations busStationObj = JSON.parseObject(busStation,BusStations.class);
        busStationsService.update(busStationObj);
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
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query",name = "fileName", value = "excel带路径文件名，比如C:\\Users\\wxm\\Desktop\\shzx_doc\\校车线路上传模版_需求_2019_0201-新格式.xls") })
    @PostMapping("/parseInfoFromExcel")
    public Result parseInfoFromExcel(@RequestParam String fileName) {
        Result banji = busStationsService.readFromExcel(fileName);
        return ResultGenerator.genSuccessResult(banji);
    }

}
