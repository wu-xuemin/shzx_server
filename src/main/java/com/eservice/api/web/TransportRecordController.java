package com.eservice.api.web;

import com.alibaba.fastjson.JSON;
import com.eservice.api.core.Result;
import com.eservice.api.core.ResultGenerator;
import com.eservice.api.model.bus_base_info.BusBaseInfo;
import com.eservice.api.model.bus_line.BusLine;
import com.eservice.api.model.bus_stations.BusStations;
import com.eservice.api.model.student.Student;
import com.eservice.api.model.student.StudentBusInfo;
import com.eservice.api.model.student.StudentInfo;
import com.eservice.api.model.transport_record.AllPickingInfo;
import com.eservice.api.model.transport_record.StationPickingInfo;
import com.eservice.api.model.transport_record.TransportRecord;
import com.eservice.api.model.transport_record.TransportRecordInfo;
import com.eservice.api.service.common.CommonService;
import com.eservice.api.service.common.Constant;
import com.eservice.api.service.impl.BusLineServiceImpl;
import com.eservice.api.service.impl.BusStationsServiceImpl;
import com.eservice.api.service.impl.StudentServiceImpl;
import com.eservice.api.service.impl.TransportRecordServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
* Class Description: xxx
* @author Wilson Hu
* @date 2018/12/17.
*/
@RestController
@RequestMapping("/transport/record")
@Api(description = "接送车次记录，一趟车对应一条记录，一趟可以对应多条学生接送信息 pickedStudentInfo")
public class TransportRecordController {

    private final Logger logger = LoggerFactory.getLogger(TransportRecordController.class);
    @Resource
    private TransportRecordServiceImpl transportRecordService;
    @Resource
    private StudentServiceImpl studentService;
    @Resource
    private BusStationsServiceImpl busStationsService;
    @Resource
    private BusLineServiceImpl busLineService;

    @Value("${debug.flag}")
    private String debugFlag;

    @ApiOperation("增加接送班次记录，上学开始行程/放学开始行程/晚班选择线时会调用该接口, flag值必须带上(上学、放学上车、放学下车、晚班) 注意参数中的日期会在服务端重置，即以服务端时间为准")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "busLine", value = " 线路ID",required = true),
            @ApiImplicitParam(paramType = "query",name = "currentStation", value = " 站点 ID",required = true),
            @ApiImplicitParam(paramType = "query",name = "busNumberInTR", value = " 校车编号 ",required = true),
            @ApiImplicitParam(paramType = "query",name = "flag", value = " 上学、放学上车、放学下车、晚班 ",required = true),
            //todo 这个status,由前端传入还是统一后端维护？
            @ApiImplicitParam(paramType = "query",name = "status", value = "行程进行中、行程已结束、晚班行程已选 ",required = true)})
    @PostMapping("/add")
    public Result add(TransportRecord transportRecord) {
        //todo, 检查记录的合理性，比如重复的.。。暂时先允许重复？
        if(transportRecord == null) {
            return ResultGenerator.genFailResult("transportRecord 为空");
        }
        if(transportRecord.getBusLine() == null) {
            return ResultGenerator.genFailResult("校车线路为空");
        }
        //线路是否存在
        if(busLineService.findBy("id",transportRecord.getBusLine()) == null){

            return ResultGenerator.genFailResult("校车线路ID 不存在");
        }

        String flag = transportRecord.getFlag();
        //APP端必须传递一个确定的flag
        if( flag == null ||
                (!flag.equals(Constant.TRANSPORT_RECORD_FLAG_MORNING) && !flag.equals(Constant.TRANSPORT_RECORD_FLAG_AFTERNOON_UP)
                        && !flag.equals(Constant.TRANSPORT_RECORD_FLAG_AFTERNOON_DOWN)
                        && !flag.equals(Constant.TRANSPORT_RECORD_FLAG_NIGHT))) {
            return ResultGenerator.genFailResult("Flag错误");
        } else {
            Date date = new Date();
            transportRecord.setDate(date);
            transportRecord.setBeginTime( new java.sql.Timestamp(date.getTime()));
//            transportRecordObj.setFlag(flag);
            if(flag.equals(Constant.TRANSPORT_RECORD_FLAG_NIGHT)) {
                //--> 晚班一些线路需要多辆校车 ，改为不需要判断是否已占线路
//                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
//                List<TransportRecord> nightRecordList = transportRecordService.getTransportRecord(null,null,Constant.BUS_MODE_NIGHT,sdf1.format(date));
//                String busNumberAlreadySelected = "";
//                Integer transportRecordId = null;
//                if(nightRecordList != null) {
//                    for (int i = 0; i < nightRecordList.size(); i++) {
//                        if(nightRecordList.get(i).getBusLine().equals(transportRecord.getBusLine())) {
//                            busNumberAlreadySelected = nightRecordList.get(i).getBusNumberInTR();
//                            transportRecordId = nightRecordList.get(i).getId();
//                            break;
//                        }
//                    }
//                    if(!"".equals(busNumberAlreadySelected)) {
//                        logger.info("线路已被" + busNumberAlreadySelected + "选择, 对应的transportRecord的ID为 " + transportRecordId);
//                        return ResultGenerator.genFailResult("线路已被[" + busNumberAlreadySelected + "]选择, 对应的transportRecord的ID为 " + transportRecordId);
//                    } else {
                        transportRecord.setStatus(Constant.TRANSPORT_RECORD_STATUS_NIGHT_LINE_SELECTED);
//                    }
//                }
            } else {
                transportRecord.setStatus(Constant.TRANSPORT_RECORD_STATUS_RUNNING);
            }
            transportRecordService.saveAndGetID(transportRecord);
            logger.info("add record  " + " busLine:" + busLineService.findById(transportRecord.getBusLine()).getName()
                    + " Flag:"+ transportRecord.getFlag() + "status-by-api:" + transportRecord.getStatus()
                    + "  busNumber:" + transportRecord.getBusNumberInTR()+ " begainTime " + transportRecord.getBeginTime());

            return ResultGenerator.genSuccessResult(transportRecord.getId());
        }
    }

    @PostMapping("/delete")
    public Result delete(@RequestParam Integer id) {
        transportRecordService.deleteById(id);
        return ResultGenerator.genSuccessResult();
    }

    @ApiOperation("上学结束行程/放学结束行程/晚班结束行程；特殊情况是晚班开始行程时也是调用此接口")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "Id", value = " ID",required = true),
            @ApiImplicitParam(paramType = "query",name = "busLine", value = " 线路ID",required = true),
            @ApiImplicitParam(paramType = "query",name = "currentStation", value = " 站点 ID"),
            @ApiImplicitParam(paramType = "query",name = "busNumberInTR", value = " 校车编号 ",required = true),
            @ApiImplicitParam(paramType = "query",name = "flag", value = " 上学、放学上车、放学下车、晚班 ",required = true),
            @ApiImplicitParam(paramType = "query",name = "status", value = "行程进行中、行程已结束、晚班行程已选 ",required = true)})
    @PostMapping("/update")
    public Result update( TransportRecord transportRecord) {

        if(transportRecord.getId() == null || transportRecord.getId() == 0){
            logger.error(" null or zero transportRecord id");
            return ResultGenerator.genFailResult(" null or zero transportRecord id");
        }
        if(transportRecord.getStatus().equals(Constant.TRANSPORT_RECORD_STATUS_DONE)){
            Date date = new Date();
            transportRecord.setEndTime( new java.sql.Timestamp(date.getTime()));
        }
        logger.info("update tr.id "+ transportRecord.getId()
                + " name: " + busLineService.findById(transportRecord.getBusLine()).getName()
                + " Flag:"+ transportRecord.getFlag() + "status:" + transportRecord.getStatus()
                + "  busNumber:" + transportRecord.getBusNumberInTR()
                + " currentStation:" + transportRecord.getCurrentStation()
        + " getBeginTime: " + transportRecord.getBeginTime());
        transportRecordService.update(transportRecord);

        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/detail")
    public Result detail(@RequestParam Integer id) {
        TransportRecord transportRecord = transportRecordService.findById(id);
        return ResultGenerator.genSuccessResult(transportRecord);
    }

    @PostMapping("/list")
    public Result list(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<TransportRecord> list = transportRecordService.findAll();
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @ApiOperation("根据日期+查询条件(学生姓名)+校车+站点+年级+班级 查询乘车记录信息，比如根据 校车+日期 获取乘车记录信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "queryStartTime", value = "查询的起始时间，比如 2018-12-19 10:16:57"),
            @ApiImplicitParam(paramType = "query",name = "queryFinishTime", value = "查询的结束时间，比如 2018-12-22 10:16:57"),
            @ApiImplicitParam(paramType = "query",name = "studentName", value = "查询的学生姓名（模糊查询），比如 小明"),
            @ApiImplicitParam(paramType = "query",name = "studentNumber", value = "查询的学生学号"),
            @ApiImplicitParam(paramType = "query",name = "busNumber", value = "查询的校车编号，比如 XC001"),
            @ApiImplicitParam(paramType = "query",name = "busMode", value = "查询的班次，限于 “上学”、“放学"),
            @ApiImplicitParam(paramType = "query",name = "busStationName", value = "查询的校车站点名称（包括了上车、下车），比如 11路口"),
            @ApiImplicitParam(paramType = "query",name = "grade", value = "查询的年级，比如 1，比如2"),
            @ApiImplicitParam(paramType = "query",name = "className", value = "查询的班级名称，比如1年级2班，比如 2年级2班"),
            @ApiImplicitParam(paramType = "query",name = "recordFlag", value = "上学、放学上车、放学下车、晚班"),
            @ApiImplicitParam(paramType = "query",name = "recordStatus", value = "行程进行中、行程已结束、晚班行程已选")
    })
    @PostMapping("/selectTransportRecord")
    public Result selectTransportRecord(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size,
                                        String queryStartTime,
                                        String queryFinishTime,
                                        String studentName,
                                        String studentNumber,
                                        String busNumber,
                                        String busMode,
                                        String busStationName,
                                        String grade,
                                        String className,
                                        String recordFlag,
                                        String recordStatus) {
        PageHelper.startPage(page, size);
        List<TransportRecordInfo> list = transportRecordService.selectTransportRecord( queryStartTime,
                                                                                       queryFinishTime,
                                                                                       studentName,
                                                                                       studentNumber,
                                                                                       busNumber,
                                                                                       busMode,
                                                                                       busStationName,
                                                                                       grade,
                                                                                       className,
                                                                                       recordFlag,
                                                                                       recordStatus);
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @ApiOperation("根据日期 查询学生乘车信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "queryStartTime", value = "要查询的起始时间，比如 2018-12-19 00:00:00 ",required = true),
            @ApiImplicitParam(paramType = "query",name = "queryFinishTime", value = "要查询的结束时间，比如 2018-12-20 00:00:00 ",required = true),
            @ApiImplicitParam(paramType = "query",name = "gradeName", value = "年级，比如 1年级，(zj) 1年级"),
            @ApiImplicitParam(paramType = "query",name = "className", value = "班级，比如 1(1)，注意括号小写"),
            @ApiImplicitParam(paramType = "query",name = "queryKey", value = "查询条件，可以是班车，姓名"),
    })
    @PostMapping("/selectAbsenceClassStudentInfo")
    public Result selectAbsenceClassStudentInfo(
                                           @RequestParam String queryStartTime,
                                           @RequestParam String queryFinishTime,
                                           String gradeName,
                                           String className,
                                           String queryKey) {
         List<StudentBusInfo> absenceStudentInfoList = transportRecordService.selectAbsenceClassStudentInfo(queryStartTime,queryFinishTime,gradeName,className,queryKey);
        return ResultGenerator.genSuccessResult(absenceStudentInfoList);
    }
    @ApiOperation("根据日期+校车编号+班次等 查询缺乘学生信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "queryStartTime", value = "要查询的起始时间，比如 2018-12-19 00:00:00 ",required = true),
            @ApiImplicitParam(paramType = "query",name = "queryFinishTime", value = "要查询的结束时间，比如 2018-12-20 00:00:00 ",required = true),
            @ApiImplicitParam(paramType = "query",name = "busNumber", value = "校车编号，比如 xc001，不填则查所有校车"),
            @ApiImplicitParam(paramType = "query",name = "busMode", value = "校车班次，限于“上学”、“放学”两种，晚班不支持 ",required = true),
            @ApiImplicitParam(paramType = "query",name = "gradeName", value = "年级，比如 1年级，(zj) 1年级"),
            @ApiImplicitParam(paramType = "query",name = "className", value = "班级，比如 1(1)，注意括号小写")
    })
    @PostMapping("/selectAbsenceStudentInfo")
    public Result selectAbsenceStudentInfo(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size,
                                           @RequestParam String queryStartTime,
                                           @RequestParam String queryFinishTime,
                                           String busNumber,
                                           @RequestParam String busMode,
                                           String gradeName,
                                           String className) {
        if(busMode.equals(Constant.BUS_MODE_AFTERNOON) || busMode.equals(Constant.BUS_MODE_MORNING)){
        } else {
            return ResultGenerator.genFailResult("busMode 只能是 " +  Constant.BUS_MODE_MORNING + " / " + Constant.BUS_MODE_AFTERNOON  );
        }
        PageHelper.startPage(page, size);
        List<StudentInfo> absenceStudentInfoList = transportRecordService.selectAbsenceStudentInfo( queryStartTime,queryFinishTime,busNumber,busMode,gradeName,className);

        return ResultGenerator.genSuccessResult(absenceStudentInfoList);
    }

    @ApiOperation("获取乘坐计划外的学生（临时乘坐）信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "busNumber", value = "校车编号", required = true),
            @ApiImplicitParam(paramType = "query",name = "busMode", value = "校车班次，限于“上学”、“放学”两种，晚班不支持 ", required = true),
            @ApiImplicitParam(paramType = "query",name = "queryStartTime", value = "要查询的起始时间，比如 2018-12-19 00:00:00 ", required = true),
            @ApiImplicitParam(paramType = "query",name = "queryFinishTime", value = "要查询的结束时间，比如 2018-12-20 00:00:00 ", required = true) })
    @PostMapping("/getUnplannedStudents")
    public Result getUnplannedStudents(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size,
                                       @RequestParam() String busNumber,
                                       @RequestParam String busMode,
                                       @RequestParam String queryStartTime,
                                       @RequestParam String queryFinishTime) {
        PageHelper.startPage(page, size);
        List<Student> list = transportRecordService.getUnplannedStudents(busNumber,busMode,queryStartTime,queryFinishTime);
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @ApiOperation("根据校车编号 + 模式（上学、放学）+日期 获取车次记录信息（包括了当前站点等）")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "busNumber", value = "校车编号" ),
            @ApiImplicitParam(paramType = "query",name = "busLineName", value = "线路名称" ),
            @ApiImplicitParam(paramType = "query",name = "busMode", value = "校车班次 "),
            @ApiImplicitParam(paramType = "query",name = "queryDate", value = "要查询的日期，比如 2018-12-19")})
    @PostMapping("/getTransportRecord")
    public Result getTransportRecord(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size,
                                     String busNumber,
                                     String busLineName,
                                     String busMode,
                                     String queryDate) {
        PageHelper.startPage(page, size);
        List<TransportRecord> list = transportRecordService.getTransportRecord(busNumber,busLineName,busMode,queryDate);
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @ApiOperation("根据日期+校车编号+班次 查询接送信息（包括了 站点列表、当前站点（可能为空，假数据导致））")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "queryStartTime", value = "查询的起始时间，比如2019-01-11 00:00:00",required = true),
            @ApiImplicitParam(paramType = "query",name = "queryFinishTime", value = "查询的结束时间，比如2019-01-12 00:00:00",required = true),
            @ApiImplicitParam(paramType = "query",name = "busNumber", value = "查询的校车编号，比如 XC006",required = true),
            @ApiImplicitParam(paramType = "query",name = "busMode", value = "查询的班次，限于 “上学”、“放学”，如果指定了班次 ",required = true)
    })
    @PostMapping("/getCurrentTransportRecord")
    public Result getCurrentTransportRecord(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size,
                                        String queryStartTime,
                                        String queryFinishTime,
                                        String busNumber,
                                        String busMode  ) {
      //  PageHelper.startPage(page, size);

        List<TransportRecordInfo> listTransportRecordInfo = transportRecordService.selectTransportRecord(queryStartTime,
                queryFinishTime,
                null,
                null,
                busNumber,
                busMode,
                null,
                null,
                null,
                null,
                null);
        if(listTransportRecordInfo.size() == 0){
            logger.info(" no record found");
            return  null;
        }
//        根据日期 +校车+班次  查询乘车记录信息，  获得站点列表，当前站点
//        返回  List<TransportRecordInfo>取第一个或者任意个元素，
//        因为 日期 +校车+班次定了，那站点列表和当前站点就都定了。
        TransportRecordInfo transportRecordInfoCurrent = listTransportRecordInfo.get(0);

        return ResultGenerator.genSuccessResult(transportRecordInfoCurrent);
    }

    @ApiOperation("根据日期(如果不填则默认当前日期)+校车编号+班次  查询该实际接送信息（包括站点列表，各个站点实际接送学生和计划接送学生））")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "queryStartTime", value = "查询的起始时间，比如 2019-01-11，参数为空则默认当天"),
            @ApiImplicitParam(paramType = "query",name = "queryFinishTime", value = "查询的结束时间，比如2019-01-12，参数为空则默认当天+1"),
            @ApiImplicitParam(paramType = "query",name = "busNumber", value = "查询的校车编号，比如 XC002",required = true),
            @ApiImplicitParam(paramType = "query",name = "busMode", value = "查询的班次， 只能“上学”、“放学”，不能查晚班因为晚班和校车没有绑定 ",required = true),
    })
    @PostMapping("/getPickingInfo")
    public Result getPickingInfo(String queryStartTime,
                                 String queryFinishTime,
                                 @RequestParam String busNumber,
                                 @RequestParam String busMode ) {
        if( !busMode.equals(Constant.BUS_MODE_AFTERNOON) && !busMode.equals(Constant.BUS_MODE_MORNING)) {
            return ResultGenerator.genFailResult("busMode 错误，限于 “上学”、“放学” ");
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if(null == queryStartTime){
            queryStartTime = sdf.format(new Date());
        }
        if(null == queryFinishTime){
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            c.add(Calendar.DAY_OF_MONTH, 1);
            Date tomorrow = c.getTime();
            queryFinishTime = sdf.format(tomorrow);
        }

        AllPickingInfo allPickingInfo = new AllPickingInfo();
        //获取当前站点
        List<TransportRecordInfo> listTransportRecordInfo = transportRecordService.selectTransportRecord(null,
                null,
                null,
                null,
                busNumber,
                busMode,
                null,
                null,
                null,
                null,
                null);

        logger.info("getPickingInfo by " + busNumber + ",Mode:" + busMode);
        if(listTransportRecordInfo.size() == 0){
            logger.info("getPickingInfo， no record found，just get plan students ");
            //即使没有乘车记录，也可以返回计划乘坐
//            return  null;
        } else {
            TransportRecordInfo transportRecordInfoCurrent = listTransportRecordInfo.get(0);
            allPickingInfo.setCurrentStation(transportRecordInfoCurrent.getCurrentStationName());
        }
        ArrayList<StationPickingInfo> pickingInfoArrayList = new ArrayList<>();

        //获取全部站点名称
        BusLine busLine = busLineService.getBusLineInfoByBusNumberAndBusMode(busNumber,busMode);
        if(busLine == null){
            return ResultGenerator.genFailResult("Can not find busLine by busNumber&busMode: " + busNumber + "&" + busMode);
        }
//        List<BusStations> stationsList = JSON.parseArray(busLine.getStations(), BusStations.class);
        //原先是JSON格式，现在是逗号分隔了站点
        if(!busMode.equals(Constant.BUS_MODE_NIGHT)) {
            String[] stationsArr = busLine.getStations().split("\\,");
            for (int i = 0; i < stationsArr.length; i++) {

                StationPickingInfo stationPickingInfo = new StationPickingInfo();
                //站点名
                stationPickingInfo.setStationName(stationsArr[i]);
                BusStations busStations = busStationsService.getBusStation(stationsArr[i]);
                if (busStations != null) {
                    // 站点都真实（即线路中的站点都来自站点列表中）之后，不会有查不到的，目前是为了防止假数据站点
                    stationPickingInfo.setStationId(busStations.getId());
                }
                List<TransportRecordInfo> listActualRecordInfo = transportRecordService.selectTransportRecord(queryStartTime,
                        queryFinishTime,
                        null,
                        null,
                        busNumber,
                        busMode,
                        stationsArr[i],
                        null,
                        null,
                        null,
                        null);
                if (debugFlag.equalsIgnoreCase("true")) {
                    logger.info("getPickingInfo，校车 " + busNumber + "在" + busMode + stationsArr[i] + queryStartTime + " 实际乘坐人数 " + listActualRecordInfo.size());
                    for (TransportRecordInfo tr : listActualRecordInfo) {
                        logger.info("实际乘坐：" + studentService.getStudentInfo(tr.getStudentNumber()).getName());
                    }
                }

                //已上车学生
                List<StudentInfo> listActualStudents = new ArrayList<>();
                for (TransportRecordInfo tri : listActualRecordInfo) {
                    StudentInfo studentInfo = new StudentInfo();
                    studentInfo.setBoardStationAfternoonName(tri.getBoardStationNameAfternoon());
                    studentInfo.setBanjiName(tri.getStudentBanji());
                    studentInfo.setBoardStationMorningName(tri.getBoardStationNameMorning());
                    studentInfo.setBusNumber(tri.getBusNumber());
                    studentInfo.setName(tri.getStudentName());
                    studentInfo.setStudentNumber(tri.getStudentNumber());
                    studentInfo.setHeadImg(tri.getStudentHeadImg());
                    listActualStudents.add(studentInfo);
                }
                stationPickingInfo.setPickedList(listActualStudents);

                //计划乘坐的学生
                List<StudentInfo> listPlannedStudents = studentService.getPlannedStudents(busNumber, busMode, stationsArr[i],null,null);
                stationPickingInfo.setPlanList(listPlannedStudents);
                pickingInfoArrayList.add(stationPickingInfo);
            }

            allPickingInfo.setStationPickingInfoList(pickingInfoArrayList);
            return ResultGenerator.genSuccessResult(allPickingInfo);
        } else {
            /**
             * 晚班没有计划乘坐的学生，也没有中间站点，只需返回实际乘坐了哪些学生即可
             * 晚班的记录，没有绑定校车信息，根据校车编号是查不到晚班记录的
             * 逻辑上不会走到这里，仅仅作为记录
             */
            logger.warn("晚班没有计划乘坐学生，查询晚班的实际上车学生则 根据返回的 transportRecord id来查");
            return ResultGenerator.genSuccessResult("晚班没有计划乘坐学生，查询晚班的实际上车学生则 根据返回的 transportRecord id来查");
        }

    }

    @ApiOperation("根据校车编号 获取校车 当天此时 所处状态（比如 上学待发车、上学进行中、上学已结束...），" +
            "如果上学没使用APP，下午放学才开始用，这是应该根据时间返回放学未开始状态" +
            "比如前端APP意外退出之后，如果是当天重启则返回奔溃前的状态，" +
            "如果是第2天或之后重启，则当作新的一天来处理")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "busNumber", value = "校车编号", required = true)})
    @PostMapping("/getBusStatusByBusNumber")
    public Result getBusStatusByBusNumber(@RequestParam() String busNumber) {
        Result status = transportRecordService.getBusStatusByBusNumber(busNumber);
        logger.info("getBusStatusByBusNumber " + busNumber + ",got status: " + status.getData());
        return status;
    }

    // never used
    @ApiOperation("根据线路名称（是唯一的）获取校车当天此时所处状态（晚班待发车、晚班线路已选、晚班进行中、晚班已结束等）" )
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "busLineName", value = "线路名称", required = true)})
    @PostMapping("/getBusStatusByBusLineName") // never used
    public Result getBusStatusByBusLineName(@RequestParam() String busLineName) {
        Result status = transportRecordService.getBusStatusByBusLineName(busLineName);
        logger.info("getBusStatusByBusLineName " + busLineName + ",got status: " + status.getData());
        return status;
    }

    @ApiOperation("根据TransportRecordId 获取该车次的实际乘坐学生，可用于晚班意外退出后恢复状态（晚班提示线路已选，会返回TransportRecordId）")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "TransportRecordId", value = "TransportRecord的Id" ) })
    @PostMapping("/getStudentsByTransportRecordId")
    public Result getStudentsByTransportRecordId(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size,
                                     String TransportRecordId) {
        PageHelper.startPage(page, size);
        List<StudentInfo> list = transportRecordService.getStudentsByTransportRecordId(TransportRecordId);
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @ApiOperation("【放学下车】过程中，分子（当前站点还没下车的）变小，根据校车获取当天放学的分子")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "busNumber", value = "校车编号" ) })
    @PostMapping("/getStudentsWaitGetOff")
    public Result getStudentsWaitGetOff(@RequestParam String busNumber ) {
        String queryStartTime;
        String queryFinishTime;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        queryStartTime = sdf.format(new Date());

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DAY_OF_MONTH, 1);
        Date tomorrow = c.getTime();
        queryFinishTime = sdf.format(tomorrow);

        AllPickingInfo allPickingInfo = new AllPickingInfo();
        ArrayList<StationPickingInfo> pickingInfoArrayList = new ArrayList<>();
        //获取全部站点名称
        BusLine busLine = busLineService.getBusLineInfoByBusNumberAndBusMode(busNumber, Constant.BUS_MODE_AFTERNOON);
        if(busLine == null){
            logger.warn("Can not find the busLine by busNumber " + busNumber + " and " + Constant.BUS_MODE_AFTERNOON);
            return ResultGenerator.genFailResult("Can not find the busLine by busNumber " + busNumber + " and " + Constant.BUS_MODE_AFTERNOON);
        }
        String[] stationsArr = busLine.getStations().split("\\,");

        for (int i = 0; i < stationsArr.length; i++) {
            StationPickingInfo stationPickingInfo = new StationPickingInfo();
            //站点名
            stationPickingInfo.setStationName(stationsArr[i]);
            BusStations busStations = busStationsService.getBusStation(stationsArr[i]);
            if (busStations != null) {
                // 站点都真实（即线路中的站点都来自站点列表中）之后，不会有查不到的，目前是为了防止假数据站点
                stationPickingInfo.setStationId(busStations.getId());
            }
            /**
             * 放学上车记录
             */
            List<TransportRecordInfo> listActualRecordInfoWubanUp = transportRecordService.selectTransportRecord(queryStartTime,
                    queryFinishTime,
                    null,
                    null,
                    busNumber,
                    //目前app传的busline参数 为上学的，不正确，这里先用null
                    null,//Constant.BUS_MODE_AFTERNOON,
                    stationsArr[i],
                    null,
                    null,
                    Constant.TRANSPORT_RECORD_FLAG_AFTERNOON_UP,
                    null);
            if (debugFlag.equalsIgnoreCase("true")) {
                logger.info("getStudentsWaitGetOff，校车 " + busNumber + " 放学上车 " + stationsArr[i] + queryStartTime + " 实际上车人数 " + listActualRecordInfoWubanUp.size());
                for (TransportRecordInfo tr : listActualRecordInfoWubanUp) {
                    logger.info("实际放学上车：" + studentService.getStudentInfo(tr.getStudentNumber()).getName());
                }
            }

            /**
             * 放学上车已上车的学生
             */
            List<StudentInfo> listActualStudentsWubanUp = new ArrayList<>();
            for (TransportRecordInfo tri : listActualRecordInfoWubanUp) {
                StudentInfo studentInfo = new StudentInfo();
                studentInfo.setBoardStationAfternoonName(tri.getBoardStationNameAfternoon());
                studentInfo.setBanjiName(tri.getStudentBanji());
                studentInfo.setBoardStationMorningName(tri.getBoardStationNameMorning());
                studentInfo.setBusNumber(tri.getBusNumber());
                studentInfo.setName(tri.getStudentName());
                studentInfo.setStudentNumber(tri.getStudentNumber());
                studentInfo.setHeadImg(tri.getStudentHeadImg());
                listActualStudentsWubanUp.add(studentInfo);
            }

            /**
             * 放学下车记录
             */
            List<TransportRecordInfo> listActualRecordInfoWubanDown = transportRecordService.selectTransportRecord(queryStartTime,
                    queryFinishTime,
                    null,
                    null,
                    busNumber,
                    //目前app传的busline参数 为上学的，不正确，这里先用null
                    null,//Constant.BUS_MODE_AFTERNOON,
                    stationsArr[i],
                    null,
                    null,
                    Constant.TRANSPORT_RECORD_FLAG_AFTERNOON_DOWN,
                    null);
            if (debugFlag.equalsIgnoreCase("true")) {
                logger.info("getStudentsWaitGetOff，校车 " + busNumber + " 放学下车 " + stationsArr[i] + queryStartTime + " 实际下车人数 " + listActualRecordInfoWubanDown.size());
                for (TransportRecordInfo tr : listActualRecordInfoWubanDown) {
                    logger.info("实际放学下车：" + studentService.getStudentInfo(tr.getStudentNumber()).getName());
                }
            }

            /**
             * 放学下车 已下车的学生
             */
            List<StudentInfo> listActualStudentsWubanDown = new ArrayList<>();
            for (TransportRecordInfo tri : listActualRecordInfoWubanDown) {
                StudentInfo studentInfo = new StudentInfo();
                studentInfo.setBoardStationAfternoonName(tri.getBoardStationNameAfternoon());
                studentInfo.setBanjiName(tri.getStudentBanji());
                studentInfo.setBoardStationMorningName(tri.getBoardStationNameMorning());
                studentInfo.setBusNumber(tri.getBusNumber());
                studentInfo.setName(tri.getStudentName());
                studentInfo.setStudentNumber(tri.getStudentNumber());
                studentInfo.setHeadImg(tri.getStudentHeadImg());
                listActualStudentsWubanDown.add(studentInfo);
            }

            /**
             * 放学上车已上车的学生 - 放学下车已下车的学生
             */
            List<StudentInfo> listActualStudentsRemain = new ArrayList<>();
            for (StudentInfo studentInfoActualWubanUp: listActualStudentsWubanUp) {

                boolean isInBus = true;
                for (StudentInfo studentInfoActualWubanDown: listActualStudentsWubanDown) {
                    /**
                     * 在实际放学下车名单中找到，表示不再在车上
                     */
                    if(studentInfoActualWubanUp.getStudentNumber().equalsIgnoreCase( studentInfoActualWubanDown.getStudentNumber())){
                        isInBus = false;
                    }
                }

                if(isInBus){
                    listActualStudentsRemain.add(studentInfoActualWubanUp);
                }
            }
            stationPickingInfo.setRemainList(listActualStudentsRemain);
            List<StudentInfo> listPlannedStudents = studentService.getPlannedStudents(busNumber, Constant.BUS_MODE_AFTERNOON, stationsArr[i],null,null);
            stationPickingInfo.setPlanList(listPlannedStudents);
            stationPickingInfo.setPickedList(listActualStudentsWubanUp);
            pickingInfoArrayList.add(stationPickingInfo);
            allPickingInfo.setStationPickingInfoList(pickingInfoArrayList);
        }
        return ResultGenerator.genSuccessResult(allPickingInfo);
    }

}
