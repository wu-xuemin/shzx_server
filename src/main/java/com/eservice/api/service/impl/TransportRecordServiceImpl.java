package com.eservice.api.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eservice.api.core.Result;
import com.eservice.api.core.ResultGenerator;
import com.eservice.api.dao.TransportRecordMapper;
import com.eservice.api.model.student.Student;
import com.eservice.api.model.student.StudentInfo;
import com.eservice.api.model.transport_record.TransportRecord;
import com.eservice.api.model.transport_record.TransportRecordInfo;
import com.eservice.api.service.TransportRecordService;
import com.eservice.api.core.AbstractService;
import com.eservice.api.service.common.CommonService;
import com.eservice.api.service.common.Constant;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
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


    public List<TransportRecordInfo> selectTransportRecord(  String queryStartTime,
                                                             String queryFinishTime,
                                                             String studentName,
                                                             String studentNumber,
                                                             String busNumber,
                                                             String busMode,
                                                             String busStationName,
                                                             String grade,
                                                             String className) {
        return transportRecordMapper.selectTransportRecord(queryStartTime,
                                                            queryFinishTime,
                                                            studentName,
                                                            studentNumber,
                                                            busNumber,
                                                            busMode,
                                                            busStationName,
                                                            grade,
                                                            className);
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

    public List<TransportRecord> getTransportRecord(String busNumber,String busMode,String queryDate){
         return transportRecordMapper.getTransportRecord(busNumber, busMode, queryDate);

    }

    public void saveAndGetID(TransportRecord transportRecord){
        transportRecordMapper.saveAndGetID(transportRecord);
    }

    /**
     *  根据校车编号 获取校车当天此时所处状态（比如早班未开始、早班进行中、早班已结束...）
     *  不返回String类型，是因为通过Sting转换后，Json的引号前都会多了斜杆，不方便前端处理。
     */
    public Result getBusStatusByBusNumber(String busNumber){
        /**
         * 如果前面没用车，比如早班没使用APP，下午午班才开始用，这是应该根据时间返回午班未开始状态
         * 比如前端APP意外退出之后，如果是当天重启则返回奔溃前的状态，如果是第2天或之后重启，则当作新的一天来处理
         */
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        List<TransportRecord> recordList = transportRecordMapper.getTransportRecord(busNumber,null,sdf1.format(date));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("TransportRecord", "");
        if(recordList.isEmpty()){
            /**
             * 如果该车在当天的 transport_record里不存在记录，表示当天没有发过车
             * 则根据时间来返回:早班待发车/午班待发车/晚班待发车 （即使存在情况：早班没使用APP，直接使用午班/晚班）
             */
            jsonObject.put("busStatus",getBusInitialStatusByTime(new Date()));
            return ResultGenerator.genSuccessResult(jsonObject);
        } else {
            /**
             * 如果该车在当天的 transport_record里存在记录，则根据最后最新那个记录beginTime和Status来判断
             */
            TransportRecord latestRecord = recordList.get(recordList.size() - 1);
            //经过String转换回导致浏览器端多出引号前的斜杆
            String busStatus = Constant.BUS_STATUS_ERROR;

            if (latestRecord != null) {
                String recordStatus = latestRecord.getStatus();
                switch (recordStatus) {
                    case Constant.TRANSPORT_RECORD_STATUS_NIGHT_LINE_SELECTED:
                        jsonObject.put("busStatus", Constant.BUS_STATUS_WANBAN_LINE_SELECTED);
                        return ResultGenerator.genSuccessResult(jsonObject);
                    case Constant.TRANSPORT_RECORD_STATUS_RUNNING:
                        jsonObject.put("TransportRecord", latestRecord.getId());
                        if (latestRecord.getFlag().equals(Constant.TRANSPORT_RECORD_FLAG_MORNING)) {
                            jsonObject.put("busStatus", Constant.BUS_STATUS_ZAOBAN_RUNNING);
                        } else if (latestRecord.getFlag().equals(Constant.TRANSPORT_RECORD_FLAG_AFTERNOON_UP)
                                || latestRecord.getFlag().equals(Constant.TRANSPORT_RECORD_FLAG_AFTERNOON_DOWN)) {
                            jsonObject.put("busStatus", Constant.BUS_STATUS_WUBAN_RUNNING);
                        } else {
                            jsonObject.put("busStatus", Constant.BUS_STATUS_WANBAN_RUNNING);
                        }
                        return ResultGenerator.genSuccessResult(jsonObject);
//                        break;
                    case Constant.TRANSPORT_RECORD_STATUS_DONE:
                        /*
                           如果已经结束的是早班，早班就被排除，根据时间来判断是午班还是晚班；
                           如果已经结束的是午班，则只考虑晚班
                         */
                        if (latestRecord.getFlag().equals(Constant.TRANSPORT_RECORD_FLAG_MORNING)) {
                            //按时间计算是早班，但是实际早班已完成，所以默认返回午班
                            String tmpStatus = getBusInitialStatusByTime(new Date());
                            if (tmpStatus.equals(Constant.BUS_STATUS_ZAOBAN_WAIT_START)) {
                                jsonObject.put("busStatus", Constant.BUS_STATUS_WUBAN_WAIT_START);
                                return ResultGenerator.genSuccessResult(jsonObject);
                            } else {
                                //如果是其他的状态，则不变
//                                busStatus = tmpStatus;
                                jsonObject.put("busStatus", tmpStatus);
                                return ResultGenerator.genSuccessResult(jsonObject);
                            }
                        } else if (latestRecord.getFlag().equals(Constant.TRANSPORT_RECORD_FLAG_AFTERNOON_UP)
                                || latestRecord.getFlag().equals(Constant.TRANSPORT_RECORD_FLAG_AFTERNOON_DOWN)) {
                            //直接切换到晚班待开始
                            jsonObject.put("busStatus", Constant.BUS_STATUS_WANBAN_WAIT_START);
                            return ResultGenerator.genSuccessResult(jsonObject);
                        } else {
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
     * 根据时间 自动判断处于 早班待发车、午班待发车、晚班待发车哪个阶段
     */
    private String getBusInitialStatusByTime( Date date ) {//Date time, SimpleDateFormat sdf
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String str = sdf.format(date);
        int hour = Integer.parseInt(str.split(":")[0]);
        if (hour < BUS_MORNING_DEADLINE) {
            return Constant.BUS_STATUS_ZAOBAN_WAIT_START;
        } else if (hour >= BUS_MORNING_DEADLINE && hour < BUS_AFTERNOON_DEADLINE) {
            /**
             * 注意，午班上车、晚班上车 另外处理
             */
            return Constant.BUS_STATUS_WUBAN_WAIT_START;
        } else if (hour >= BUS_AFTERNOON_DEADLINE) {
            return Constant.BUS_STATUS_WANBAN_WAIT_START;
        } else {
            return Constant.BUS_STATUS_ERROR;
        }
    }
}
