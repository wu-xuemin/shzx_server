package com.eservice.api.web;
import com.eservice.api.core.Result;
import com.eservice.api.core.ResultGenerator;
import com.eservice.api.model.student.Student;
import com.eservice.api.model.student.StudentInfo;
import com.eservice.api.model.transport_record.TransportRecord;
import com.eservice.api.model.transport_record.TransportRecordInfo;
import com.eservice.api.service.common.Constant;
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
import java.util.ArrayList;
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

    @Value("${debug.flag}")
    private String debugFlag;

    @ApiOperation("增加接送班次记录，一趟车对应一条记录，比如在开始行程时调用该接口, 注意参数中的日期会在服务端重置，即以服务端时间为准")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "date", value = " 时间，留空")})
    @PostMapping("/add")
    public Result add(TransportRecord transportRecord) {
        if(transportRecord == null) {
            return ResultGenerator.genFailResult("transportRecord 不能为空");
        }
        transportRecord.setDate(new Date());
        transportRecordService.saveAndGetID(transportRecord);
        return ResultGenerator.genSuccessResult(transportRecord.getId());
    }

    @PostMapping("/delete")
    public Result delete(@RequestParam Integer id) {
        transportRecordService.deleteById(id);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/update")
    public Result update(TransportRecord transportRecord) {
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
                                        String busNumber,
                                        String busMode,
                                        String busStationName,
                                        String grade,
                                        String className) {
        PageHelper.startPage(page, size);
        List<TransportRecordInfo> list = transportRecordService.selectTransportRecord( queryStartTime,
                                                                                       queryFinishTime,
                                                                                       studentName,
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
            @ApiImplicitParam(paramType = "query",name = "queryStartTime", value = "要查询的起始时间，比如 2018-12-19 00:00:00 "),
            @ApiImplicitParam(paramType = "query",name = "queryFinishTime", value = "要查询的结束时间，比如 2018-12-20 00:00:00 "),
            @ApiImplicitParam(paramType = "query",name = "busNumber", value = "校车编号，比如 xc001"),
            @ApiImplicitParam(paramType = "query",name = "busMode", value = "校车班次，限于“早班”、“午班”两种，晚班不支持 ")
    })
    @PostMapping("/selectAbsenceStudentInfo")
    public Result selectAbsenceStudentInfo(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size,
                                           @RequestParam String queryStartTime,
                                           @RequestParam String queryFinishTime,
                                           @RequestParam String busNumber,
                                           @RequestParam String busMode ) {
        PageHelper.startPage(page, size);

        if(busMode.equals(Constant.BUS_MODE_AFTERNOON) || busMode.equals(Constant.BUS_MODE_MORNING)){
        } else {
            return ResultGenerator.genFailResult("busMode 只能是 " +  Constant.BUS_MODE_MORNING + " / " + Constant.BUS_MODE_AFTERNOON  );
        }

        /**
         * 先获取计划乘坐该校车该班次的所有学生，再获取该日期该班次的乘车记录
         */
        List<StudentInfo> listPlannedStudents = studentService.getPlannedStudentsByBusNumberAndBusMode(busNumber, busMode);

        if(debugFlag.equalsIgnoreCase("true")) {
            logger.info("校车 " + busNumber + busMode + "班次" + " 计划乘坐学生人数 " + listPlannedStudents.size());
            for (StudentInfo st : listPlannedStudents) {
                logger.info(" 具体学号：" + st.getStudentNumber());
            }
        }
        List<TransportRecordInfo> listActualRecordInfo = transportRecordService.selectTransportRecord(queryStartTime,
                                                                                                    queryFinishTime,
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
            @ApiImplicitParam(paramType = "query",name = "busNumber", value = "校车编号", required = true),
            @ApiImplicitParam(paramType = "query",name = "busMode", value = "校车班次，限于“早班”、“午班”两种，晚班不支持 ", required = true),
            @ApiImplicitParam(paramType = "query",name = "queryDate", value = "要查询的日期，比如 2018-12-19", required = true)})
            //@ApiImplicitParam(paramType = "query",name = "queryFinishTime", value = "要查询的结束时间，比如 2018-12-20 00:00:00 ", required = true)
    @PostMapping("/getTransportRecord")
    public Result getTransportRecord(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size,
                                       @RequestParam() String busNumber,
                                       @RequestParam String busMode,
                                       @RequestParam String queryDate) {
        PageHelper.startPage(page, size);
        List<TransportRecord> list = transportRecordService.getTransportRecord(busNumber,busMode,queryDate);
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }
}
