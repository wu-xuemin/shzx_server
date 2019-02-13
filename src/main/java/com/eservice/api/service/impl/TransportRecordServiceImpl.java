package com.eservice.api.service.impl;

import com.eservice.api.dao.TransportRecordMapper;
import com.eservice.api.model.student.Student;
import com.eservice.api.model.student.StudentInfo;
import com.eservice.api.model.transport_record.TransportRecord;
import com.eservice.api.model.transport_record.TransportRecordInfo;
import com.eservice.api.service.TransportRecordService;
import com.eservice.api.core.AbstractService;
import com.eservice.api.service.common.CommonService;
import com.eservice.api.service.common.Constant;
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

    public List<TransportRecordInfo> selectTransportRecord(  String queryStartTime,
                                                             String queryFinishTime,
                                                             String studentName,
                                                             String busNumber,
                                                             String busMode,
                                                             String busStationName,
                                                             String grade,
                                                             String className) {
        return transportRecordMapper.selectTransportRecord(queryStartTime,
                                                            queryFinishTime,
                                                            studentName,
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
     */
    public String getBusStatusByBusNumber( String busNumber){
        /**
         * 如果前面没用车，比如早班没使用APP，下午午班才开始用，这是应该根据时间返回午班未开始状态
         * 比如前端APP意外退出之后，如果是当天重启则返回奔溃前的状态，如果是第2天或之后重启，则当作新的一天来处理
         */
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        List<TransportRecord> recordList = transportRecordMapper.getTransportRecord(busNumber,null,sdf1.format(date));
        if(recordList.isEmpty()){
            /**
             * 如果该车在当天的 transport_record里不存在记录，表示当天没有发过车
             * 则根据时间来返回:早班待发车/午班待发车/晚班待发车 （即使存在情况：早班没使用APP，直接使用午班/晚班）
             */
            return CommonService.getBusStatusByTime(new Date());
        } else {
            /**
             * 如果该车在当天的 transport_record里存在记录，则根据最后最新那个记录beginTime和Status来判断
             */
            String status = recordList.get(recordList.size() - 1).getStatus();
            SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");
            String beginTime = sdf2.format(recordList.get(recordList.size() - 1).getBeginTime());
            int h = Integer.parseInt(beginTime.split(":")[0]);
            if(h <=12){
                if(status.equals(Constant.TRANSPORT_RECORD_STATUS_RUNNING)) {
                    return Constant.BUS_STATUS_ZAOBAN_RUNNING;
                }else if(status.equals(Constant.TRANSPORT_RECORD_STATUS_DONE)) {
                    return Constant.BUS_STATUS_ZAOBAN_DONE;
                } else {
                    return "Wrong status" + status;
                }
            } else if( h>12 && h<= 17){
                if(status.equals(Constant.TRANSPORT_RECORD_STATUS_RUNNING)) {
                    return Constant.BUS_STATUS_WUBAN_RUNNING;
                }else if(status.equals(Constant.TRANSPORT_RECORD_STATUS_DONE)) {
                    return Constant.BUS_STATUS_WUBAN_DONE;
                } else {
                    return "Wrong status" + status;
                }
            } else if( h>17 && h<= 24){
                if(status.equals(Constant.TRANSPORT_RECORD_STATUS_RUNNING)) {
                    return Constant.BUS_STATUS_WANBAN_RUNNING;
                }else if(status.equals(Constant.TRANSPORT_RECORD_STATUS_DONE)) {
                    return Constant.BUS_STATUS_WANBAN_DONE;
                } else {
                    return "Wrong status" + status;
                }
            } else {
                return "Wrong Hour" + h;
            }
        }
    }
}
