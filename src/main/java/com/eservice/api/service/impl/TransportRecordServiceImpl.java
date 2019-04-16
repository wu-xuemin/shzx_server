package com.eservice.api.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eservice.api.core.Result;
import com.eservice.api.core.ResultGenerator;
import com.eservice.api.dao.TransportRecordMapper;
import com.eservice.api.model.student.Student;
import com.eservice.api.model.student.StudentBusInfo;
import com.eservice.api.model.student.StudentInfo;
import com.eservice.api.model.transport_record.TransportRecord;
import com.eservice.api.model.transport_record.TransportRecordInfo;
import com.eservice.api.service.TransportRecordService;
import com.eservice.api.core.AbstractService;
import com.eservice.api.service.common.CommonService;
import com.eservice.api.service.common.Constant;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.tomcat.util.bcel.Const;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
* Class Description: xxx
* @author Wilson Hu
* @date 2018/12/17.
*/
@Service
@Transactional
public class TransportRecordServiceImpl extends AbstractService<TransportRecord> implements TransportRecordService {
    @Resource
    private TransportRecordMapper transportRecordMapper;
    @Value("${bus_morning_deadline}")
    private Integer BUS_MORNING_DEADLINE;

    @Value("${bus_afternoon_deadline}")
    private Integer BUS_AFTERNOON_DEADLINE;

    private final Logger logger = LoggerFactory.getLogger(TransportRecordServiceImpl.class);

    @Resource
    private TransportRecordServiceImpl transportRecordService;

    @Value("${debug.flag}")
    private String debugFlag;
	
    @Resource
    private StudentServiceImpl studentService;
    /**
     *  注意，晚班的记录，没有绑定校车信息，根据校车编号是查不到晚班记录的
     */
    public List<TransportRecordInfo> selectTransportRecord(  String queryStartTime,
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
        return transportRecordMapper.selectTransportRecord(queryStartTime,
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
    }

     public List<Student> getUnplannedStudents(String busNumber,String busMode, String queryStartTime, String queryFinishTime){
        if(Constant.BUS_MODE_MORNING.equalsIgnoreCase(busMode)) {
            return transportRecordMapper.getUnplannedStudentsMorning(busNumber, queryStartTime, queryFinishTime);
        }else if(Constant.BUS_MODE_AFTERNOON.equalsIgnoreCase(busMode)){
            return transportRecordMapper.getUnplannedStudentsAfternoon(busNumber, queryStartTime, queryFinishTime);
        } else {
            return null;
        }
     }

    public List<TransportRecord> getTransportRecord(String busNumber,String busLineName,String busMode,String queryDate){
         return transportRecordMapper.getTransportRecord(busNumber,busLineName, busMode, queryDate);

    }

    public void saveAndGetID(TransportRecord transportRecord){
        transportRecordMapper.saveAndGetID(transportRecord);
    }

    /**
     *  根据校车编号 获取校车当天此时所处状态（比如上学未开始、上学进行中、上学已结束...）
     *  不返回String类型，是因为通过Sting转换后，Json的引号前都会多了斜杆，不方便前端处理。
     */
    public Result getBusStatusByBusNumber(String busNumber){
        /**
         * 如果前面没用车，比如上学没使用APP，下午放学才开始用，这是应该根据时间返回放学未开始状态
         * 比如前端APP意外退出之后，如果是当天重启则返回奔溃前的状态，如果是第2天或之后重启，则当作新的一天来处理
         */
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        //晚班线路和校车不绑定，无法根据校车来查
        List<TransportRecord> recordList = transportRecordMapper.getTransportRecord(busNumber,null,null,sdf1.format(date));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("TransportRecord", "");
        if(recordList.isEmpty()){
            /**
             * 如果该车在当天的 transport_record里不存在记录，表示当天没有发过车
             * 则根据时间来返回:上学待发车/放学待发车/晚班待发车 （即使存在情况：上学没使用APP，直接使用放学/晚班）
             */
            logger.info("today record is empty by busNumber " + busNumber);
            jsonObject.put("busStatus",getBusInitialStatusByTime(new Date()));
            return ResultGenerator.genSuccessResult(jsonObject);
        } else {
            /**
             * 如果该车在当天的 transport_record里存在记录，则根据最后最新那个记录beginTime和Status来判断
             */
            TransportRecord latestRecord = recordList.get(recordList.size() - 1);
            //经过String转换回导致浏览器端多出引号前的斜杆
            String busStatus = Constant.BUS_STATUS_ERROR;

            logger.info("getBusStatusByBusNumber "+busNumber+", today last record id/flag/status is " +latestRecord.getId() + "/" +latestRecord.getFlag() + "/" + latestRecord.getStatus());
            if (latestRecord != null) {
                String recordStatus = latestRecord.getStatus();
                jsonObject.put("TransportRecord", latestRecord.getId());
                switch (recordStatus) {
                    case Constant.TRANSPORT_RECORD_STATUS_NIGHT_LINE_SELECTED:
//                        jsonObject.put("TransportRecord", latestRecord.getId());
                        jsonObject.put("busStatus", Constant.BUS_STATUS_WANBAN_LINE_SELECTED);
                        return ResultGenerator.genSuccessResult(jsonObject);
                    case Constant.TRANSPORT_RECORD_STATUS_RUNNING:
//                        jsonObject.put("TransportRecord", latestRecord.getId());
                        if (latestRecord.getFlag().equals(Constant.TRANSPORT_RECORD_FLAG_MORNING)) {
                            jsonObject.put("busStatus", Constant.BUS_STATUS_ZAOBAN_RUNNING);
                        } else if (latestRecord.getFlag().equals(Constant.TRANSPORT_RECORD_FLAG_AFTERNOON_UP) ) {
                            jsonObject.put("busStatus", Constant.BUS_STATUS_WUBAN_ABOARDING);
                        } else if (latestRecord.getFlag().equals(Constant.TRANSPORT_RECORD_FLAG_AFTERNOON_DOWN)) {
                            jsonObject.put("busStatus", Constant.BUS_STATUS_WUBAN_RUNNING);
                        } else if(latestRecord.getFlag().equals(Constant.TRANSPORT_RECORD_FLAG_NIGHT)){
                            jsonObject.put("busStatus", Constant.BUS_STATUS_WANBAN_RUNNING);
                        } else {
                            return ResultGenerator.genFailResult("invalid flag: " + latestRecord.getFlag() );
                        }
                        return ResultGenerator.genSuccessResult(jsonObject);
//                        break;
                    case Constant.TRANSPORT_RECORD_STATUS_DONE:
                        /*
                           如果已经结束的是上学，上学就被排除，根据时间来判断是放学还是晚班；
                           如果已经结束的是放学，则只考虑晚班
                         */
                        if (latestRecord.getFlag().equals(Constant.TRANSPORT_RECORD_FLAG_MORNING)) {
                            //最后的记录是上学已完成
                            String tmpStatus = getBusInitialStatusByTime(new Date());
                            if (tmpStatus.equals(Constant.BUS_STATUS_ZAOBAN_WAIT_START)) {
                                jsonObject.put("busStatus", Constant.BUS_STATUS_WUBAN_WAIT_START);
                                return ResultGenerator.genSuccessResult(jsonObject);
                            } else {
                                //如果是其他的状态(放学待发车，晚班待发车)，则不变
                                jsonObject.put("busStatus", tmpStatus);
                                return ResultGenerator.genSuccessResult(jsonObject);
                            }
                        } else if (latestRecord.getFlag().equals(Constant.TRANSPORT_RECORD_FLAG_AFTERNOON_UP)) {
                            //最后的记录是 放学上车已完成
                            jsonObject.put("busStatus", Constant.BUS_STATUS_WUBAN_RUNNING);
                            return ResultGenerator.genSuccessResult(jsonObject);
                        } else if (latestRecord.getFlag().equals(Constant.TRANSPORT_RECORD_FLAG_AFTERNOON_DOWN)) {
                            //最后的记录是 放学下车已完成
                            jsonObject.put("busStatus", Constant.BUS_STATUS_WANBAN_WAIT_START);
                            return ResultGenerator.genSuccessResult(jsonObject);
                        } else if (latestRecord.getFlag().equals(Constant.TRANSPORT_RECORD_FLAG_NIGHT)) {
                            //最后的记录是 晚班已完成
                            jsonObject.put("busStatus", Constant.BUS_STATUS_WANBAN_DONE);
                            return ResultGenerator.genSuccessResult(jsonObject);
                        }
                    default:
                        busStatus = Constant.BUS_STATUS_ERROR;
                }
            }
            if (busStatus.equals(Constant.BUS_STATUS_ERROR)) {
                return ResultGenerator.genFailResult(busStatus);
            } else {
                return ResultGenerator.genSuccessResult(busStatus);
            }
        }
    }

    /**
     * 根据晚班线路名称 获取校车 当天此时 所处状态（晚班待发车、晚班线路已选、晚班进行中、晚班已结束）"
     * 可以合并
     */
    public Result getBusStatusByBusLineName(String busLineName){
        /**
         * 如果前面没用车，比如上学没使用APP，下午放学才开始用，这是应该根据时间返回放学未开始状态
         * 比如前端APP意外退出之后，如果是当天重启则返回奔溃前的状态，如果是第2天或之后重启，则当作新的一天来处理
         */
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        //晚班线路和校车不绑定，无法根据校车来查
        List<TransportRecord> recordList = transportRecordMapper.getTransportRecord(null,busLineName,null,sdf1.format(date));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("TransportRecord", "");
        if(recordList.isEmpty()){
            /**
             * 如果该车在当天的 transport_record里不存在记录，表示当天没有发过车
             * 则根据时间来返回:上学待发车/放学待发车/晚班待发车 （即使存在情况：上学没使用APP，直接使用放学/晚班）
             */
            logger.info("today record is empty by busLineName " + busLineName);
            jsonObject.put("busStatus",getBusInitialStatusByTime(new Date()));
            return ResultGenerator.genSuccessResult(jsonObject);
        } else {
            /**
             * 如果该车在当天的 transport_record里存在记录，则根据最后最新那个记录beginTime和Status来判断
             */
            TransportRecord latestRecord = recordList.get(recordList.size() - 1);
            //经过String转换回导致浏览器端多出引号前的斜杆
            String busStatus = Constant.BUS_STATUS_ERROR;
            logger.info("today last record id/flag/status by busLineName " +busLineName + " is " +latestRecord.getId() + "/" +latestRecord.getFlag() + "/" + latestRecord.getStatus());
            if (latestRecord != null) {
                String recordStatus = latestRecord.getStatus();
                switch (recordStatus) {
                    case Constant.TRANSPORT_RECORD_STATUS_NIGHT_LINE_SELECTED:
                        jsonObject.put("TransportRecord", latestRecord.getId());
                        jsonObject.put("busStatus", Constant.BUS_STATUS_WANBAN_LINE_SELECTED);
                        return ResultGenerator.genSuccessResult(jsonObject);
                    case Constant.TRANSPORT_RECORD_STATUS_RUNNING:
                        jsonObject.put("TransportRecord", latestRecord.getId());
                        if (latestRecord.getFlag().equals(Constant.TRANSPORT_RECORD_FLAG_MORNING)) {
                            jsonObject.put("busStatus", Constant.BUS_STATUS_ZAOBAN_RUNNING);
                        } else if (latestRecord.getFlag().equals(Constant.TRANSPORT_RECORD_FLAG_AFTERNOON_UP)) {
                            jsonObject.put("busStatus", Constant.BUS_STATUS_WUBAN_ABOARDING);
                        } else if (latestRecord.getFlag().equals(Constant.TRANSPORT_RECORD_FLAG_AFTERNOON_DOWN)) {
                            jsonObject.put("busStatus", Constant.BUS_STATUS_WUBAN_RUNNING);
                        } else {
                            jsonObject.put("busStatus", Constant.BUS_STATUS_WANBAN_RUNNING);
                        }
                        return ResultGenerator.genSuccessResult(jsonObject);
//                        break;
                    case Constant.TRANSPORT_RECORD_STATUS_DONE:
                        /*
                           如果已经结束的是上学，上学就被排除，根据时间来判断是放学还是晚班；
                           如果已经结束的是放学，则只考虑晚班
                         */
                        if (latestRecord.getFlag().equals(Constant.TRANSPORT_RECORD_FLAG_MORNING)) {
                            //最后的记录是上学已完成
                            String tmpStatus = getBusInitialStatusByTime(new Date());
                            if (tmpStatus.equals(Constant.BUS_STATUS_ZAOBAN_WAIT_START)) {
                                jsonObject.put("busStatus", Constant.BUS_STATUS_WUBAN_WAIT_START);
                                return ResultGenerator.genSuccessResult(jsonObject);
                            } else {
                                //如果是其他的状态(放学待发车，晚班待发车)，则不变
                                jsonObject.put("busStatus", tmpStatus);
                                return ResultGenerator.genSuccessResult(jsonObject);
                            }
                        } else if (latestRecord.getFlag().equals(Constant.TRANSPORT_RECORD_FLAG_AFTERNOON_UP)) {
                            //最后的记录是 放学上车已完成
                            jsonObject.put("busStatus", Constant.BUS_STATUS_WUBAN_RUNNING);
                            return ResultGenerator.genSuccessResult(jsonObject);
                        } else if (latestRecord.getFlag().equals(Constant.TRANSPORT_RECORD_FLAG_AFTERNOON_DOWN)) {
                            //最后的记录是 放学下车已完成
                            jsonObject.put("busStatus", Constant.BUS_STATUS_WANBAN_WAIT_START);
                            return ResultGenerator.genSuccessResult(jsonObject);
                        } else if (latestRecord.getFlag().equals(Constant.TRANSPORT_RECORD_FLAG_NIGHT)) {
                            //最后的记录是 晚班已完成
                            jsonObject.put("busStatus", Constant.BUS_STATUS_WANBAN_DONE);
                            return ResultGenerator.genSuccessResult(jsonObject);
                        }
                    default:
                        busStatus = Constant.BUS_STATUS_ERROR;
                }
            }
            if (busStatus.equals(Constant.BUS_STATUS_ERROR)) {
                return ResultGenerator.genFailResult(busStatus);
            } else {
                return ResultGenerator.genSuccessResult(busStatus);
            }
        }
    }

    /**
     * 在没有当天的发车记录的情况下
     * 根据时间 自动判断处于 上学待发车、放学待发车、晚班待发车哪个阶段
     */
    private String getBusInitialStatusByTime( Date date ) {//Date time, SimpleDateFormat sdf
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String str = sdf.format(date);
        int hour = Integer.parseInt(str.split(":")[0]);
        if (hour < BUS_MORNING_DEADLINE) {
            return Constant.BUS_STATUS_ZAOBAN_WAIT_START;
        } else if (hour >= BUS_MORNING_DEADLINE && hour < BUS_AFTERNOON_DEADLINE) {
            return Constant.BUS_STATUS_WUBAN_WAIT_START;
        } else if (hour >= BUS_AFTERNOON_DEADLINE) {
            return Constant.BUS_STATUS_WANBAN_WAIT_START;
        } else {
            return Constant.BUS_STATUS_ERROR;
        }
    }

    public List<StudentInfo> getStudentsByTransportRecordId(String TransportRecordId ){
        return transportRecordMapper.getStudentsByTransportRecordId(TransportRecordId);
    }

    public List<StudentInfo> selectAbsenceStudentInfo(
                                           String queryStartTime,
                                           String queryFinishTime,
                                           String busNumber,
                                           String busMode,
                                           String gradeName,
                                           String className) {

        /**
         * 先获取计划乘坐该校车该班次的所有学生，再获取该日期该班次的乘车记录
         */
        List<StudentInfo> listPlannedStudents = studentService.getPlannedStudents(busNumber, busMode,null,gradeName,className);

        if(debugFlag.equalsIgnoreCase("true")) {
            logger.info("selectAbsenceStudentInfo，校车 " + busNumber + busMode + "班次" + " 计划乘坐学生人数 " + listPlannedStudents.size());
//            for (StudentInfo st : listPlannedStudents) {
//                logger.info(" 具体学号：" + st.getStudentNumber() + st.getName());
//            }
        }
        List<TransportRecordInfo> listActualRecordInfo = transportRecordService.selectTransportRecord(queryStartTime,
                queryFinishTime,
                null,
                null,
                busNumber,
                busMode,
                null,
                gradeName,
                className,
                null,
                null);
        if(debugFlag.equalsIgnoreCase("true")) {
            logger.info("selectAbsenceStudentInfo by 校车 " + busNumber + "在" + busMode + " " +  queryStartTime + " 实际乘坐人数 " + listActualRecordInfo.size());
//            for (TransportRecordInfo tr: listActualRecordInfo) {
//                logger.info(" 具体学号：" + tr.getStudentNumber());
//            }
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
            logger.info("selectAbsenceStudentInfo by 校车 " + busNumber + "在" + busMode + " " + queryStartTime + " 缺乘人数 " + listAbsenceStudents.size());
            for (StudentInfo st : listAbsenceStudents) {
                logger.info(" 缺乘：" + st.getStudentNumber() + studentService.getStudentInfo(st.getStudentNumber()).getName());
            }
        }
        return listAbsenceStudents;
    }

    /**
     * 按班级查询学生乘车情况
     * @param queryStartTime 开始时间戳
     * @param queryFinishTime 结束时间戳
     * @param gradeName 年级
     * @param className 班级
     * @param queryKey 查询条件
     * @return
     */
    public List<StudentBusInfo> selectAbsenceClassStudentInfo(String queryStartTime,
                                                           String queryFinishTime,
                                                           String gradeName,
                                                           String className,
                                                           String queryKey){
        List<StudentBusInfo> listPlannedStudents= studentService.getPlannedClassStudent(queryKey,className,gradeName);
        /*if(debugFlag.equalsIgnoreCase("true")) {
            logger.info("selectAbsenceClassStudentInfo by 查询内容 " + queryKey +"时间"+  queryStartTime );
            for (StudentBusInfo tr: listPlannedStudents) {
                logger.info(" 具体学号：" + tr.getStudentNumber());
            }
        }*/

        List<TransportRecordInfo> listActualRecordInfo = transportRecordMapper.selectRecordStudent(gradeName,className,queryKey,queryStartTime,queryFinishTime);
        /**
         * 判断是缺乘记录，有记录则为true，没有为false
         */
        for (int i=0;i<listPlannedStudents.size();i++){
            for (TransportRecordInfo transportRecordInfo:
            listActualRecordInfo) {
                if(listPlannedStudents.get(i).getStudentNumber().equalsIgnoreCase(transportRecordInfo.getStudentNumber())){
                    StudentBusInfo studentBusInfo= listPlannedStudents.get(i);
                    if (transportRecordInfo.getMode().equals(Constant.BUS_MODE_MORNING)){
                        studentBusInfo.setMorningAttendance(true);
                        listPlannedStudents.set(i,studentBusInfo);
                    }else if(transportRecordInfo.getMode().equals(Constant.BUS_MODE_AFTERNOON)){
                        studentBusInfo.setAfterAttendance(true);
                        listPlannedStudents.set(i,studentBusInfo);
                    }
                }
            }
        }

        return listPlannedStudents;
    }
}
