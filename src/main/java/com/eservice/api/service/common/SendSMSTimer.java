package com.eservice.api.service.common;

import com.eservice.api.model.banji.Banji;
import com.eservice.api.model.transport_record.TransportRecord;
import com.eservice.api.model.user.User;
import com.eservice.api.service.impl.BanjiServiceImpl;
import com.eservice.api.service.impl.TransportRecordServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.backoff.Sleeper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 定时把检查是否发送短信
 */
@Component
public class SendSMSTimer {
    private final static Logger logger = LoggerFactory.getLogger(SendSMSTimer.class);
    @Resource
    private TransportRecordServiceImpl transportRecordService;

    @Resource
    private BanjiServiceImpl banjiService;

    @Resource
    private SMSUtils smsUtils;

    /**
     * 内部测试时使用，避免发短信到老师那边, TODO：正式应用时改为false
     */
    private boolean justTest = true;

    /**
     * 测试
     * 每分钟          0 0/1 * * * ?
     * 每天 08:30     0 30 08 * * ?
     * 参考： http://cron.qqe2.com/
     */
    @Scheduled(cron = "0 30 08 * * ?")
    public void sendOnMorning() {
        logger.info("cron coming ： 0 30 08 * * ? ");
        if(!todayIsSchoolDay()){
            logger.info("Not school day " + new Date());
            return;
        }
        //所有班级：
        List<Banji> banjiList = banjiService.findAll();
        //对应的所有班主任
        List<User> bzrList = banjiService.getChargeTeachers();
        //  班主任电话，size为1的数组
        ArrayList< String[] > bzrPhoneList = new ArrayList<>();
        for (int i = 0; i <bzrList.size() ; i++) {
            if(bzrList.get(i).getPhone() != null) {
                //“Fake” 只是为了转为数组形式
                bzrPhoneList.add(bzrList.get(i).getPhone().split("Fake"));
            } else {
                logger.info(bzrList.get(i).getName() + ", Phone is empty");
            }
        }

        String strAbsenceDetail = null;
        if(justTest){
            // 测试用
            String[] testerPhoneArr = new String[]{"15715766877","13588027825"};//{"13588027825"}; //
            for (int i = 0; i <3; i++) {
                strAbsenceDetail = banjiService.getAbsenceTodayByGradeClass(banjiList.get(i).getGrade(),banjiList.get(i).getClassName(),Constant.BUS_MODE_MORNING);
                logger.info("Try send 短信内容：" + strAbsenceDetail);
                smsUtils.send(testerPhoneArr,strAbsenceDetail);
                try {
                    //连续不停的发送，会丢失短信
                    Thread.sleep(10*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else {
            // 正式用
            for (int i = 0; i <banjiList.size() ; i++) {
                strAbsenceDetail = banjiService.getAbsenceTodayByGradeClass(banjiList.get(i).getGrade(),banjiList.get(i).getClassName(),Constant.BUS_MODE_MORNING);
                // bzrPhoneList 和 banjiList 一一对应
                logger.info(i + "Try send 短信内容：" + strAbsenceDetail);
                smsUtils.send(bzrPhoneList.get(i),strAbsenceDetail);
                try {
                    //连续不停的发送，会丢失短信
                    Thread.sleep(10*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                logger.info( "Sent SMS to " + bzrPhoneList.get(i) + strAbsenceDetail);
            }
        }

    }
    @Scheduled(cron = "0 30 17 * * ?")
    public void sendOnAfternoon() {
        logger.info("cron coming ： 0 30 17 * * ? ");
        if(!todayIsSchoolDay()){
            logger.info("Not school day " + new Date());
            return;
        }

        //1-8年级的班级：
        List<Banji> banji1to8List = banjiService.getBanji1to8List();
        //9-12年级除外的班级，即1-8年级才需要发送缺乘短信
        List<User> bzr1To8GradeList = banjiService.get1To8GradeChargeTeachers();
        //  班主任电话，size为1的数组
        ArrayList< String[] > bzr1To8GradePhoneList = new ArrayList<>();
        for (int i = 0; i <bzr1To8GradeList.size() ; i++) {
            if(bzr1To8GradeList.get(i).getPhone() != null) {
                //“Fake” 只是为了转为数组形式
                bzr1To8GradePhoneList.add(bzr1To8GradeList.get(i).getPhone().split("Fake"));
            } else {
                logger.info(bzr1To8GradeList.get(i).getName() + ", Phone is empty");
            }
        }

        String strAbsenceDetail = null;
        if(justTest){
            // 测试用
            String[] testerPhoneArr = new String[]{"15715766877","13588027825"}; //{"15715766877","13588027825"}
            for (int i = 0; i <2; i++) {
                strAbsenceDetail = banjiService.getAbsenceTodayByGradeClass(banji1to8List.get(i).getGrade(),banji1to8List.get(i).getClassName(),Constant.BUS_MODE_AFTERNOON);
                logger.info("Try send 短信内容：" + strAbsenceDetail);
                smsUtils.send(testerPhoneArr,strAbsenceDetail);
                try {
                    //连续不停的发送，会丢失短信; 另外短信本身有一定的丢失率。目前看百十来次有一次。
                    Thread.sleep(10*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else {
            // 正式用。 1-8年级才需要发送缺乘短信
            for (int i = 0; i <bzr1To8GradePhoneList.size() ; i++) {
                strAbsenceDetail = banjiService.getAbsenceTodayByGradeClass(banji1to8List.get(i).getGrade(),banji1to8List.get(i).getClassName(),Constant.BUS_MODE_AFTERNOON);
                logger.info(i + "下午 Try send 短信内容：" + strAbsenceDetail);
                smsUtils.send(bzr1To8GradePhoneList.get(i),strAbsenceDetail);
                try {
                    //连续不停的发送，会丢失短信；另外短信本身有一定的丢失率。
                    Thread.sleep(10*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                logger.info( "Sent SMS to " + bzr1To8GradePhoneList.get(i)[0] + "，内容:" + strAbsenceDetail);
            }
        }

    }
    
    /**
     * 判断是否为上学日
     */
    public boolean todayIsSchoolDay() {
        Date date = new Date();
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        List<TransportRecord> recordList = transportRecordService.getTransportRecord(null, null, null, sdf1.format(date));

        /**
         * 有3个记录以上，判断为当天为上学日，避免个别情况误触发
         */
        if (recordList.size() > 3) {
            return true;
        } else {
            return false;
        }
    }
}
