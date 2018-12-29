package com.eservice.api.web;
import com.eservice.api.core.Result;
import com.eservice.api.core.ResultGenerator;
import com.eservice.api.model.bus_stations.BusStations;
import com.eservice.api.service.BusStationsService;
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
@RequestMapping("/bus/stations")
@Api("校车站点管理")
public class BusStationsController {
    @Resource
    private BusStationsService busStationsService;

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
}
