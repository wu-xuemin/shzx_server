package com.eservice.api.web;

import com.alibaba.fastjson.JSON;
import com.eservice.api.core.Result;
import com.eservice.api.core.ResultGenerator;
import com.eservice.api.model.bus_base_info.BusBaseInfo;
import com.eservice.api.model.bus_line.BusLine;
import com.eservice.api.model.bus_stations.BusStations;
import com.eservice.api.model.student.Student;
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

    @ApiOperation("增加接送班次记录，早班开始行程/午班开始行程/晚班选择线时会调用该接口, flag值必须带上(早班、午班上车、午班下车、晚班) 注意参数中的日期会在服务端重置，即以服务端时间为准")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "busLine", value = " 线路ID",required = true),
            @ApiImplicitParam(paramType = "query",name = "currentStation", value = " 站点 ID",required = true),
            @ApiImplicitParam(paramType = "query",name = "busNumberInTR", value = " 校车编号 ",required = true),
            @ApiImplicitParam(paramType = "query",name = "flag", value = " 早班、午班上车、午班下车、晚班 ",required = true),
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
                //晚班是在选择线路时创建record,所以需要判断线路是否被占用
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                List<TransportRecord> nightRecordList = transportRecordService.getTransportRecord(null,null,Constant.BUS_MODE_NIGHT,sdf1.format(date));
                String busNumber = "";
                if(nightRecordList != null) {
                    for (int i = 0; i < nightRecordList.size(); i++) {
                        if(nightRecordList.get(i).getBusLine().equals(transportRecord.getBusLine())) {
                            busNumber = nightRecordList.get(i).getBusNumberInTR();
                            break;
                        }
                    }
                    if(!"".equals(busNumber)) {
                        return ResultGenerator.genFailResult("线路已被[" + busNumber + "]选择");
                    } else {
                        transportRecord.setStatus(Constant.TRANSPORT_RECORD_STATUS_NIGHT_LINE_SELECTED);
                    }
                }
            } else {
                transportRecord.setStatus(Constant.TRANSPORT_RECORD_STATUS_RUNNING);
            }
            transportRecordService.saveAndGetID(transportRecord);
            return ResultGenerator.genSuccessResult(transportRecord.getId());
        }
    }

    @PostMapping("/delete")
    public Result delete(@RequestParam Integer id) {
        transportRecordService.deleteById(id);
        return ResultGenerator.genSuccessResult();
    }

    @ApiOperation("早班结束行程/午班结束行程/晚班结束行程；特殊情况是晚班开始行程时也是调用此接口")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "Id", value = " ID",required = true),
            @ApiImplicitParam(paramType = "query",name = "busLine", value = " 线路ID",required = true),
            @ApiImplicitParam(paramType = "query",name = "currentStation", value = " 站点 ID",required = true),
            @ApiImplicitParam(paramType = "query",name = "busNumberInTR", value = " 校车编号 ",required = true),
            @ApiImplicitParam(paramType = "query",name = "flag", value = " 早班、午班上车、午班下车、晚班 ",required = true),
            @ApiImplicitParam(paramType = "query",name = "status", value = "行程进行中、行程已结束、晚班行程已选 ",required = true)})
    @PostMapping("/update")
    public Result update(TransportRecord transportRecord) {
        if(transportRecord.getStatus().equals(Constant.TRANSPORT_RECORD_STATUS_DONE)){
            Date date = new Date();
            transportRecord.setEndTime( new java.sql.Timestamp(date.getTime()));
        }
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
            @ApiImplicitParam(paramType = "query",name = "busMode", value = "查询的班次，限于 “早班”、“午班"),
            @ApiImplicitParam(paramType = "query",name = "busStationName", value = "查询的校车站点名称（包括了上车、下车），比如 11路口"),
            @ApiImplicitParam(paramType = "query",name = "grade", value = "查询的年级，比如 1，比如2"),
            @ApiImplicitParam(paramType = "query",name = "className", value = "查询的班级名称，比如1年级2班，比如 2年级2班")
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
                                        String className) {
        PageHelper.startPage(page, size);
        List<TransportRecordInfo> list = transportRecordService.selectTransportRecord( queryStartTime,
                                                                                       queryFinishTime,
                                                                                       studentName,
                                                                                       studentNumber,
                                                                                       busNumber,
                                                                                       busMode,
                                                                                       busStationName,
                                                                                       grade,
                                                                                       className);
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @ApiOperation("根据日期+校车编号+班次 查询缺乘学生信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "queryStartTime", value = "要查询的起始时间，比如 2018-12-19 00:00:00 ",required = true),
            @ApiImplicitParam(paramType = "query",name = "queryFinishTime", value = "要查询的结束时间，比如 2018-12-20 00:00:00 ",required = true),
            @ApiImplicitParam(paramType = "query",name = "busNumber", value = "校车编号，比如 xc001，不填则查所有校车"),
            @ApiImplicitParam(paramType = "query",name = "busMode", value = "校车班次，限于“早班”、“午班”两种，晚班不支持 ",required = true)
    })
    @PostMapping("/selectAbsenceStudentInfo")
    public Result selectAbsenceStudentInfo(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size,
                                           @RequestParam String queryStartTime,
                                           @RequestParam String queryFinishTime,
                                           String busNumber,
                                           @RequestParam String busMode ) {
        PageHelper.startPage(page, size);

        if(busMode.equals(Constant.BUS_MODE_AFTERNOON) || busMode.equals(Constant.BUS_MODE_MORNING)){
        } else {
            return ResultGenerator.genFailResult("busMode 只能是 " +  Constant.BUS_MODE_MORNING + " / " + Constant.BUS_MODE_AFTERNOON  );
        }

        /**
         * 先获取计划乘坐该校车该班次的所有学生，再获取该日期该班次的乘车记录
         */
        List<StudentInfo> listPlannedStudents = studentService.getPlannedStudents(busNumber, busMode,null);

        if(debugFlag.equalsIgnoreCase("true")) {
            logger.info("校车 " + busNumber + busMode + "班次" + " 计划乘坐学生人数 " + listPlannedStudents.size());
            for (StudentInfo st : listPlannedStudents) {
                logger.info(" 具体学号：" + st.getStudentNumber());
            }
        }
        List<TransportRecordInfo> listActualRecordInfo = transportRecordService.selectTransportRecord(queryStartTime,
                                                                                                    queryFinishTime,
                                                                                                    null,
                                                                                                    null,
                                                                                                    busNumber,
                                                                                                    busMode,
                                                                                                    null,
                                                                                                    null,
                                                                                                    null);
        if(debugFlag.equalsIgnoreCase("true")) {
            logger.info("校车 " + busNumber + "在" + busMode + " " +  queryStartTime + " 实际乘坐人数 " + listActualRecordInfo.size());
            for (TransportRecordInfo tr: listActualRecordInfo) {
                logger.info(" 具体学号：" + tr.getStudentNumber());
            }
        }

        List<StudentInfo> listActualStudents = new ArrayList<>();
        for (TransportRecordInfo tri:listActualRecordInfo) {
            StudentInfo studentInfo = new StudentInfo();
            studentInfo.setBanjiName(tri.getStudentBanji());
            studentInfo.setBoardStationAfternoonName(tri.getBoardStationNameAfternoon());
            studentInfo.setBoardStationMorningName(tri.getBoardStationNameMorning());
            studentInfo.setBusNumber(tri.getBusNumber());
            studentInfo.setName(tri.getStudentName());
            studentInfo.setStudentNumber(tri.getStudentNumber());
            listActualStudents.add(studentInfo);
        }

        /**
         * 缺乘学生 = 计划乘坐学生 - 实际乘坐
         */
        List<StudentInfo> listAbsenceStudents = new ArrayList<>();
        for (StudentInfo plannedStudent: listPlannedStudents) {

            boolean absence = true;
            for (StudentInfo actualStudent: listActualStudents) {
                /**
                 * 在实际乘坐的名单中找到，表示没有缺乘
                 */
                if(plannedStudent.getStudentNumber().equalsIgnoreCase( actualStudent.getStudentNumber())){
                    absence = false;
                }
            }

            if(absence){
                listAbsenceStudents.add(plannedStudent);
            }
        }

        if(debugFlag.equalsIgnoreCase("true")) {
            logger.info("校车 " + busNumber + "在" + busMode + " " + queryStartTime + " 缺乘人数 " + listAbsenceStudents.size());
            for (StudentInfo st : listAbsenceStudents) {
                logger.info(" 缺乘 具体学号：" + st.getStudentNumber());
            }
        }
        PageInfo pageInfo = new PageInfo(listAbsenceStudents);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @ApiOperation("获取乘坐计划外的学生（临时乘坐）信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "busNumber", value = "校车编号", required = true),
            @ApiImplicitParam(paramType = "query",name = "busMode", value = "校车班次，限于“早班”、“午班”两种，晚班不支持 ", required = true),
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

    @ApiOperation("根据校车编号 + 模式（早班、午班）+日期 获取车次记录信息（包括了当前站点等）")
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
            @ApiImplicitParam(paramType = "query",name = "busMode", value = "查询的班次，限于 “早班”、“午班”，如果指定了班次 ",required = true)
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
            @ApiImplicitParam(paramType = "query",name = "queryStartTime", value = "查询的起始时间，比如 2019-01-11"),
            @ApiImplicitParam(paramType = "query",name = "queryFinishTime", value = "查询的结束时间，比如2019-01-12"),
            @ApiImplicitParam(paramType = "query",name = "busNumber", value = "查询的校车编号，比如 XC002",required = true),
            @ApiImplicitParam(paramType = "query",name = "busMode", value = "查询的班次，限于 “早班”、“午班”，如果指定了班次 ",required = true),
    })
    @PostMapping("/getPickingInfo")
    public Result getPickingInfo(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size,
                                           String queryStartTime,
                                           String queryFinishTime,
                                          @RequestParam String busNumber,
                                          @RequestParam String busMode ) {
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
                null);
        if(listTransportRecordInfo.size() == 0){
            logger.info(" no record found，just ");
            //即使没有乘车记录，也可以返回计划乘坐
//            return  null;
        } else {
            TransportRecordInfo transportRecordInfoCurrent = listTransportRecordInfo.get(0);
            allPickingInfo.setCurrentStation(transportRecordInfoCurrent.getCurrentStationName());
        }
        ArrayList<StationPickingInfo> list = new ArrayList<>();

        //获取全部站点名称
        BusLine busLine = busLineService.getBusLineInfoByBusNumberAndBusMode(busNumber,busMode);

//        List<BusStations> stationsList = JSON.parseArray(busLine.getStations(), BusStations.class);
        //原先是JSON格式，现在是逗号分隔了站点
         String[] stationsArr = busLine.getStations().split("\\,");
        for (int i = 0; i < stationsArr.length; i++) {

            StationPickingInfo stationPickingInfo = new StationPickingInfo();
            //站点名
            stationPickingInfo.setStationName(stationsArr[i]);
            BusStations busStations = busStationsService.getBusStation(stationsArr[i]);
            if(busStations != null){
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
                    null);
            if(debugFlag.equalsIgnoreCase("true")) {
                logger.info("校车 " + busNumber + "在" + busMode + " " +  queryStartTime + " 实际乘坐人数 " + listActualRecordInfo.size());
                for (TransportRecordInfo tr: listActualRecordInfo) {
                    logger.info(" 具体学号：" + tr.getStudentNumber());
                }
            }

            //已上车学生
            List<StudentInfo> listActualStudents = new ArrayList<>();
            for (TransportRecordInfo tri:listActualRecordInfo) {
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

            //总学生
            List<StudentInfo> listPlannedStudents = studentService.getPlannedStudents(busNumber, busMode,stationsArr[i]);
            stationPickingInfo.setPlanList(listPlannedStudents);
            list.add(stationPickingInfo);
        }

        allPickingInfo.setStationPickingInfoList(list);

        return ResultGenerator.genSuccessResult(allPickingInfo);
    }


    @ApiOperation("根据校车编号 获取校车 当天此时 所处状态（比如 早班待发车、早班进行中、早班已结束...），" +
            "如果早班没使用APP，下午午班才开始用，这是应该根据时间返回午班未开始状态" +
            "比如前端APP意外退出之后，如果是当天重启则返回奔溃前的状态，" +
            "如果是第2天或之后重启，则当作新的一天来处理")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "busNumber", value = "校车编号", required = true)})
    @PostMapping("/getBusStatusByBusNumber")
    public Result getBusStatusByBusNumber(@RequestParam() String busNumber) {
        Result status = transportRecordService.getBusStatusByBusNumber(busNumber);
        return status;
    }


    @ApiOperation("根据线路名称（是唯一的）获取校车当天此时所处状态（晚班待发车、晚班线路已选、晚班进行中、晚班已结束等）" )
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "busLineName", value = "线路名称", required = true)})
    @PostMapping("/getBusStatusByBusLineName")
    public Result getBusStatusByBusLineName(@RequestParam() String busLineName) {
        Result status = transportRecordService.getBusStatusByBusLineName(busLineName);
        return status;
    }
}
