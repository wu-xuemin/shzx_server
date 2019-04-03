package com.eservice.api.service.common;

import com.eservice.api.model.banji.Banji;
import com.eservice.api.model.user.User;
import com.eservice.api.service.impl.BanjiServiceImpl;
import com.eservice.api.service.impl.TransportRecordServiceImpl;
import com.eservice.api.service.impl.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 定时把未结束的行程结束掉
 */
@Component
public class SendSMS {
    private final static Logger logger = LoggerFactory.getLogger(SendSMS.class);
    @Resource
    private TransportRecordServiceImpl transportRecordService;

    @Value("${debug.flag}")
    private String Time1ToSendSMS;
    @Resource
    private BanjiServiceImpl banjiService; 

    /**
     * 测试
     * 每分钟          0 0/1 * * * ?
     * 每天 08:30     0 30 08 * * ?  todo 节假日接口，暑假寒假
     * 参考： http://cron.qqe2.com/
     * 注意： 当天记录当天清，不要写到第2天去。
     */



    @Scheduled(cron = "0 30 08 ? * MON-FRI")
    public void sendOnMorning() {

        //所有班级
        List<Banji> banjiList = banjiService.findAll();
        //在职的班主任
        List<User> bzrList = banjiService.getChargeTeachers();
    }
    @Scheduled(cron = "0 30 17 ? * MON-FRI")
    public void sendOnAfternoon() {

        //所有班级
        List<Banji> banjiList = banjiService.findAll();

        //在职的班主任
        List<User> bzrList = banjiService.getChargeTeachers();

    }

}
