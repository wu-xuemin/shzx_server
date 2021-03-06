package com.eservice.api.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eservice.api.core.Result;
import com.eservice.api.core.ResultGenerator;
import com.eservice.api.model.bus_base_info.BusBaseFullInfo;
import com.eservice.api.model.bus_base_info.BusBaseInfo;
import com.eservice.api.model.user.User;
import com.eservice.api.service.common.CommonService;
import com.eservice.api.service.common.Constant;
import com.eservice.api.service.impl.BusBaseInfoServiceImpl;
import com.eservice.api.service.impl.UserServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.Date;
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

    @Resource
    private UserServiceImpl userService;

    private final Logger logger = LoggerFactory.getLogger(BusBaseInfoController.class);

    @PostMapping("/add")
    public Result add(String busBaseInfo) {
        BusBaseInfo busBaseInfoObj = JSON.parseObject(busBaseInfo, BusBaseInfo.class);
        busBaseInfoObj.setCreateTime(new Date());
        busBaseInfoService.save(busBaseInfoObj);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/delete")
    public Result delete(@RequestParam String busBaseInfo) {
        if(busBaseInfo != null) {
            BusBaseInfo busBaseInfoObj = JSON.parseObject(busBaseInfo, BusBaseInfo.class);
            busBaseInfoObj.setValid(Constant.VALID_NO);
            busBaseInfoService.update(busBaseInfoObj);
        } else {
            ResultGenerator.genFailResult("参数不能为空！");
        }
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/update")
    public Result update(String busBaseInfo) {
        BusBaseInfo busBaseInfoObj = JSON.parseObject(busBaseInfo, BusBaseInfo.class);
        busBaseInfoObj.setUpdateTime(new Date());
        busBaseInfoService.update(busBaseInfoObj);
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
        List<BusBaseInfo> list = busBaseInfoService.listByNumber();
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @ApiOperation("根据校车编号等 去查询校车详情包括巴士妈妈名字等")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "busNumber", value = " 校车编号"),
            @ApiImplicitParam(paramType = "query", name = "busLineName", value = " 线路名称"),
            @ApiImplicitParam(paramType = "query", name = "busDriverAccount", value = " 司机账号"),
            @ApiImplicitParam(paramType = "query", name = "busMomAccount", value = " 巴士妈妈账号"),
            @ApiImplicitParam(paramType = "query", name = "busSupplierName", value = " 供应商名称"),
            @ApiImplicitParam(paramType = "query", name = "schoolPartition", value = " 校区"),
            @ApiImplicitParam(paramType = "query", name = "keyWord", value = " 关键字, 查询线路名称、校车编号、busMom账号、司机账号、校车供应商名称")
    })
    @PostMapping("/getBusBaseInfo")
    public Result getBusBaseInfo(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size,
                                 String busNumber,
                                 String busLineName,
                                 String busDriverAccount,
                                 String busMomAccount,
                                 String busSupplierName,
                                 String schoolPartition,
                                 String keyWord
                                 ) {
        PageHelper.startPage(page, size);
        List<BusBaseFullInfo> list = busBaseInfoService.getBusBaseInfo(busNumber, busLineName, busDriverAccount, busMomAccount, busSupplierName,schoolPartition,keyWord);
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @ApiOperation("从xls excel里读取校车信息")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query",name = "fileName", value = "excel带路径文件名，比如C:\\Users\\wxm\\Desktop\\shzx_doc\\xxxx.xls") })
    @PostMapping("/parseInfoFromExcel")
    public Result parseInfoFromExcel(@RequestParam String fileName) {
        Result banji = busBaseInfoService.readFromExcel(fileName);
        return ResultGenerator.genSuccessResult(banji);
    }

    @ApiOperation("根据busMom账号获取其所在校车的编号")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query",name = "busMomAccount", value = "busMom的账号，比如高明娟") })
    @PostMapping("/getBusNumberByBusMomAccount")
    public Result getBusNumberByBusMomAccount(@RequestParam String busMomAccount) {
        String busNumber = busBaseInfoService.getBusNumberByBusMomAccount(busMomAccount);
        return ResultGenerator.genSuccessResult(busNumber);
    }

    @ApiOperation("参数传入上中的班车URL，根据URL返回的数据，创建校车信息（包括校区，校车编号，车牌号，busMom，创建时间，是否有效），不包括司机。返回新增的 校车数量 ")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query",name = "urlStr", value = " url地址 ")})
    @PostMapping("/getURLContentAndCreateBusBaseInfo")
    public Result getURLContentAndCreateBusBaseInfo(@RequestParam(defaultValue = Constant.SHZX_URL_GET_BUS)
                                                            String urlStr) {
        Integer addedBusBaseInfoSum = 0;
        addedBusBaseInfoSum = busBaseInfoService.getURLContentAndCreateBusBaseInfo(urlStr);
        return ResultGenerator.genSuccessResult("busBaseInfo " + addedBusBaseInfoSum + " is added");
    }

    @ApiOperation("更新校车的司机和busMom字段，其实这些接口是可以不需要的，但是目前的excel/URL前前后后不同造成")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", name = "urlStr", value = " url地址 ") })
    @PostMapping("/updateBusBaseInfoDriverAndBusMom")
    public Result updateBusBaseInfoDriverAndBusMom(@RequestParam(defaultValue = Constant.SHZX_URL_GET_BUS)
                                                      String urlStr) {

        Result result = busBaseInfoService.updateBusBaseInfoDriverAndBusMom(urlStr);
        return result;
    }
}
