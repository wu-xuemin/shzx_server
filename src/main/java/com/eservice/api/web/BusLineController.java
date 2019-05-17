package com.eservice.api.web;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eservice.api.core.Result;
import com.eservice.api.core.ResultGenerator;
import com.eservice.api.model.bus_base_info.BusBaseInfo;
import com.eservice.api.model.bus_line.BusLine;
import com.eservice.api.model.bus_line.BusLineInfo;
import com.eservice.api.model.student.StudentInfo;
import com.eservice.api.service.common.CommonService;
import com.eservice.api.service.impl.BusBaseInfoServiceImpl;
import com.eservice.api.service.impl.BusLineServiceImpl;
import com.eservice.api.service.impl.BusStationsServiceImpl;
import com.eservice.api.service.impl.StudentServiceImpl;
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
import com.eservice.api.service.common.Constant;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.mybatis.mapper.entity.Condition;

/**
* Class Description: xxx
* @author Wilson Hu
* @date 2019/01/11.
*/
@RestController
@RequestMapping("/bus/line")
@Api(description = "校车线路信息")
public class BusLineController {
    @Resource
    private BusLineServiceImpl busLineService;
    @Resource
    private BusStationsServiceImpl busStationsService;
    @Resource
    private BusBaseInfoServiceImpl busBaseInfoService;
    @Resource
    private StudentServiceImpl studentService;

    private final Logger logger = LoggerFactory.getLogger(BusLineController.class);

    @PostMapping("/add")
    public Result add(String busLine) {
        BusLine busLineObj = JSON.parseObject(busLine, BusLine.class);
        busLineObj.setCreateTime(new Date());
        busLineService.save(busLineObj);
        return ResultGenerator.genSuccessResult();
    }

    /**
     * 删除只设置valid值为0
     *
     * @param busLine
     * @return
     */
    @PostMapping("/delete")
    public Result delete(@RequestParam String busLine) {
        if (busLine != null) {
            BusLine busLineObj = JSON.parseObject(busLine, BusLine.class);
            busLineObj.setValid(Constant.VALID_NO);
            busLineService.update(busLineObj);
        } else {
            ResultGenerator.genFailResult("参数不能为空！");
        }
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/update")
    public Result update(String busLine) {
        BusLine busLineObj = JSON.parseObject(busLine, BusLine.class);
        busLineObj.setUpdateTime(new Date());
        busLineService.update(busLineObj);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/detail")
    public Result detail(@RequestParam Integer id) {
        BusLine busLine = busLineService.findById(id);
        return ResultGenerator.genSuccessResult(busLine);
    }

    @PostMapping("/list")
    public Result list(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size, String queryKey) {
        PageHelper.startPage(page, size);
        List<BusLine> list;
        if ("".equals(queryKey)) {
            list = busLineService.listByName();
        } else {
            list = busLineService.list(queryKey);
        }

        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @PostMapping("/getBusLineByBusNumber")
    public Result getBusLineByBusNumber(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size, String busNumber) {
        PageHelper.startPage(page, size);
        List<BusLine> list;
        list = busLineService.getBusLineByBusNumber(busNumber);
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @ApiOperation("根据巴士妈妈账号 获得巴士线路等信息,每个巴士妈妈都固定一辆校车，会返回固定一辆校车的上学和放学线路（busNumber相同）")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "busMomAccount", value = "巴士妈妈账号，具有唯一性", required = true)})
    @PostMapping("/getBusLineInfoByBusMomAccount")
    public Result getBusLineInfoByBusMomAccount(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size,
                                                @RequestParam String busMomAccount) {
        PageHelper.startPage(page, size);
        List<BusLineInfo> list = busLineService.getBusLineInfoByBusMomAccount(busMomAccount);
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @ApiOperation("根据巴士司机账号 来获得巴士线路等信息,每个司机都固定一辆校车，会返回固定一辆校车的上学和放学线路（busNumber相同）")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "busDriverAccount", value = "巴士司机账号，具有唯一性", required = true)})
    @PostMapping("/getBusLineInfoByBusDriverAccount")
    public Result getBusLineInfoByBusDriverAccount(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size,
                                                   @RequestParam String busDriverAccount) {
        PageHelper.startPage(page, size);
        List<BusLineInfo> list = busLineService.getBusLineInfoByBusDriverAccount(busDriverAccount);
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @ApiOperation("根据校区来获得该校区的所有巴士线路相关信息")
    @PostMapping("/getBusLineInfoBySchoolPartition")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", name = "schoolPartition", value = "校区，目前只有“浦东”、“浦西” ", required = true)})
    public Result getBusLineInfoBySchoolPartition(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size,
                                                  @RequestParam String schoolPartition) {
        PageHelper.startPage(page, size);
        List<BusLineInfo> list = busLineService.getBusLineInfoBySchoolPartition(schoolPartition);
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @ApiOperation("根据校车编号/上学放学 来获得该校车/上学放学的所有学生")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "busNumber", value = "校车编号，比如 xc001", required = true),
            @ApiImplicitParam(paramType = "query", name = "busMode", value = " 上学 放学,不填则不限制")
    })
    @PostMapping("/getStudents")
    public Result getStudents(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size,
                              @RequestParam String busNumber,
                              String busMode) {
        PageHelper.startPage(page, size);
        List<StudentInfo> list = busLineService.getStudents(busNumber, busMode);
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @ApiOperation("根据校车编号/上学放学 来获得该校车的线路信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "busNumber", value = "校车编号，比如 xc001", required = true),
            @ApiImplicitParam(paramType = "query", name = "busMode", value = " 上学 放学")
    })
    @PostMapping("/getBusLineInfoByBusNumberAndBusMode")
    public Result getBusLineInfoByBusNumberAndBusMode(@RequestParam String busNumber,
                                                      @RequestParam String busMode) {

        BusLine busLine = busLineService.getBusLineInfoByBusNumberAndBusMode(busNumber, busMode);
        return ResultGenerator.genSuccessResult(busLine);
    }

    @ApiOperation("根据 早午晚班 来查询线路信息")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", name = "busMode", value = " 上学 放学 晚班", required = true)
    })
    @PostMapping("/getBusLinesByMode")
    public Result getBusLinesByMode(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size,
                                    @RequestParam String busMode) {
        PageHelper.startPage(page, size);
        List<BusLine> busLineList = busLineService.getBusLinesByMode(busMode);
        PageInfo pageInfo = new PageInfo(busLineList);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @ApiOperation("从xls excel里读取线路信息")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", name = "fileName", value = "excel带路径文件名，比如C:\\Users\\wxm\\Desktop\\shzx_doc\\xxxxx.xls")})
    @PostMapping("/parseInfoFromExcel")
    public Result parseInfoFromExcel(@RequestParam String fileName) {
        Result banji = busLineService.readFromExcel(fileName);
        return ResultGenerator.genSuccessResult(banji);
    }

    @ApiOperation("清除放学线路再根据上学线路生成放学线路（除少部分线路的上学放学线路相同，大部分线路的上学放学相反）返回生成的放学线路")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", name = "busLineIDsNotReserve", value = "上学放学相同的线路，用逗号隔开，默认值 " + Constant.BUS_LINE_ZAOBAN_WUBAN_SAME)})
    @PostMapping("/cleanAndCreateAfternoonBusLine")
    public Result cleanAndCreateAfternoonBusLine(@RequestParam(defaultValue = Constant.BUS_LINE_ZAOBAN_WUBAN_SAME) String busLineIDsNotReserve) {

        logger.info("传入的不需倒序的busLineIDs：" + busLineIDsNotReserve);
        busLineService.cleanAndCreateAfternoonBusLine(busLineIDsNotReserve);
        return ResultGenerator.genSuccessResult("不需要倒序的ID " + busLineIDsNotReserve);
    }

    @ApiOperation("参数传入上中的班车URL， 根据URL返回的数据，创建线路（包括线路名称，校车，班次，是否有效，创建时间，站点列表）。返回新增的 线路数量 ")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", name = "urlStr", value = " url地址 ")})
    @PostMapping("/getURLContentAndCreateBusSLine")
    public Result getURLContentAndCreateBusSLine(@RequestParam(defaultValue = Constant.SHZX_URL_GET_BUS)
                                                         String urlStr) {
        Integer addedBusLineSum = 0;
        String strFromUrl = CommonService.getUrlResponse(urlStr);
        try {
            JSONObject jsonObject = JSON.parseObject(strFromUrl);
            JSONArray ja = jsonObject.getJSONArray("result");

            for (int i = 0; i < ja.size(); i++) {
                ///  "id":"1",  "remark":"07:23",  "station_name":"蒙自路蒙自西路口"
                BusLine busLine = new BusLine();
                JSONObject jo = ja.getJSONObject(i);
                String busNumber = jo.getString("id");
                String remarkTime = jo.getString("remark");
                String stationName = jo.getString("station_name");

                busLine.setName(busNumber + "号车_上学");

                BusBaseInfo busBaseInfo = busBaseInfoService.findBy("number", busNumber);
                if (busBaseInfo == null) {
                    logger.warn("can not find bus by bus number " + busNumber);
                } else {
                    busLine.setBusBaseInfo(busBaseInfo.getId());
                }
                busLine.setMode(Constant.BUS_MODE_MORNING);
                busLine.setValid(Constant.VALID_YES);
                busLine.setCreateTime(new Date());

                /**
                 * 逐个加入站点
                 * 根据线路名字查询线路
                 */
                Class cl = Class.forName("com.eservice.api.model.bus_line.BusLine");
                Field field = cl.getDeclaredField("name");
                BusLine busLineExist = null;
                busLineExist = busLineService.findBy(field.getName(), busLine.getName());

                /**
                 * 如果线路不存在，则增加线路
                 */
                if (null == busLineExist) {
                    busLine.setStations(stationName);
                    busLineService.save(busLine);
                    logger.info("busLine added: =====" + busLine.getName() + "/" + busLine.getMode() + "/" + busLine.getStations());
                    addedBusLineSum++;
                } else {
                    String currentStations = busLineExist.getStations();
                    if (currentStations == null) {
                        /**
                         * 如果线路存在，而该站点为空，则增加站点（目前用逗号隔离站点），并更新线路
                         * （这个只是为了单独更新站点字段，因为URL的站点信息改了，清掉这个字段内容，重新导入时用到）
                         */
                        logger.info("站点 " + stationName + ", 还不存在，现在加入");
                        currentStations = stationName;

                        busLine.setId(busLineExist.getId());
                        busLine.setStations(currentStations);
                        busLine.setUpdateTime(new Date());
                        logger.info("now, currentStations: " + currentStations + "is updated to busLine " + busLine.getName());
                        busLineService.update(busLine);
                    } else if (busLineExist.getStations().contains(stationName)) {
                        /**
                         * 如果线路存在，而该站点也存在，则不做处理
                         */
                    } else {
                        /**
                         * 如果线路存在，而该站点不存在，则增加站点（目前用逗号隔离站点），并更新线路
                         */
                        logger.info("站点 " + stationName + ", 还不存在，现在加入");
                        currentStations = currentStations + "," + stationName;

                        busLine.setId(busLineExist.getId());
                        busLine.setStations(currentStations);
                        busLine.setUpdateTime(new Date());
                        logger.info("now, currentStations: " + currentStations + "is updated to busLine " + busLine.getName());
                        busLineService.update(busLine);
                    }
                }

            }

            /**
             * TODO: 每条线路，按时间顺序重新排站点
             */

            /**
             * 放学线路重新生成
             */
//            busLineService.cleanAndCreateAfternoonBusLine(Constant.BUS_LINE_ZAOBAN_WUBAN_SAME);
            /**
             * 放学线路更新站点
             */
            busLineService.updateAfternoonBusLineStation(Constant.BUS_LINE_ZAOBAN_WUBAN_SAME);

        } catch (Exception e) {
            logger.warn(" exception: " + e.toString());
            return ResultGenerator.genFailResult(" exception: " + e.toString());
        }
        return ResultGenerator.genSuccessResult("addedBusLineSum " + addedBusLineSum + " is added");
    }
}
