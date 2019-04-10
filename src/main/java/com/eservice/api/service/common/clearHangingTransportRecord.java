package com.eservice.api.service.common;

import com.eservice.api.model.transport_record.TransportRecord;
import com.eservice.api.service.impl.TransportRecordServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 定时把未结束的行程结束掉
 */
@Component
class ClearHangingTransportRecord {
    private final static Logger logger = LoggerFactory.getLogger(ClearHangingTransportRecord.class);
    @Resource
    private TransportRecordServiceImpl transportRecordService;
    /**
     * 每天01：00清理
     * 测试
     * 每分钟          0 0/1 * * * ?
     * 每天 23:30     0 30 23 * * ?
     * 参考： http://cron.qqe2.com/
     * 注意： 当天记录当天清，不要写到第2天去。
     */

    @Scheduled(cron = "0 30 23 * * ?")
    public void clear() {
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        List<TransportRecord> recordList = transportRecordService.getTransportRecord(null,null,null,sdf1.format(date));
        if(recordList.isEmpty()){
            logger.info("none transportRecord need clear");
            return;
        }
        for(TransportRecord tr:recordList){
            if( !tr.getStatus().equals(Constant.TRANSPORT_RECORD_STATUS_DONE)){

                logger.warn( "清理了 校车编号:" + tr.getBusNumberInTR() +
                        "  Flag:" + tr.getFlag() +
                        "  Status:" + tr.getStatus() + " ==> " + Constant.TRANSPORT_RECORD_STATUS_DONE) ;
                tr.setStatus(Constant.TRANSPORT_RECORD_STATUS_DONE);
                tr.setEndTime( new java.sql.Timestamp(date.getTime()));
                transportRecordService.update(tr);
            }
        }
    }

}
