package com.eservice.api.service.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eservice.api.model.student.Student;
import com.eservice.api.model.transport_record.TransportRecord;
import com.eservice.api.service.impl.TransportRecordServiceImpl;
import com.eservice.api.service.park.ResponseCode;
import com.eservice.api.service.park.TokenService;
import com.eservice.api.service.park.model.Condition;
import com.eservice.api.service.park.model.RepoIdBean;
import com.eservice.api.service.park.model.ResponseModel;
import com.eservice.api.service.park.model.WinVisitorRecord;
import com.eservice.api.service.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 定时把未结束的行程结束掉
 */
@Component
public class clearHangingTransportRecord {
    private final static Logger logger = LoggerFactory.getLogger(clearHangingTransportRecord.class);
    @Resource
    private TransportRecordServiceImpl transportRecordService;
    /**
     * 每天01：00清理
     * 测试 每分钟  * 0/1 * * * ?
     */
    //todo 测试有问题
    @Scheduled(cron = "* 0/1 * * * ?")
    public void clear() {
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        List<TransportRecord> recordList = transportRecordService.getTransportRecord(null,null,sdf1.format(date));
        if(recordList.isEmpty()){
            logger.info("none transportRecord need clear");
        }
        for(TransportRecord tr:recordList){
            if( !tr.getStatus().equals(Constant.TRANSPORT_RECORD_STATUS_DONE)){
                tr.setStatus(Constant.TRANSPORT_RECORD_STATUS_DONE);
                logger.warn( "清理了 校车编号:" + tr.getBusNumberInTR() +
                        "  Flag:" + tr.getFlag() +
                        "  Status:" + tr.getStatus()) ;
            }
        }
    }

}
