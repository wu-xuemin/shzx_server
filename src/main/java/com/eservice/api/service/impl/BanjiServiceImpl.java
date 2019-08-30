package com.eservice.api.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eservice.api.core.Result;
import com.eservice.api.core.ResultGenerator;
import com.eservice.api.dao.BanjiMapper;
import com.eservice.api.model.banji.Banji;
import com.eservice.api.model.banji.BanjiExcel;
import com.eservice.api.model.banji.BanjiInfo;
import com.eservice.api.model.bus_base_info.BusBaseInfo;
import com.eservice.api.model.bus_line.BusLine;
import com.eservice.api.model.student.StudentInfo;
import com.eservice.api.model.user.User;
import com.eservice.api.service.BanjiService;
import com.eservice.api.core.AbstractService;
import com.eservice.api.service.common.CommonService;
import com.eservice.api.service.common.Constant;
import com.github.pagehelper.PageInfo;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.io.*;
import java.lang.reflect.Field;
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
@Service
@Transactional
public class BanjiServiceImpl extends AbstractService<Banji> implements BanjiService {
    @Resource
    private BanjiMapper banjiMapper;
    @Resource
    private BanjiServiceImpl banjiService;
    @Resource
    private UserServiceImpl userService;
    @Resource
    private TransportRecordServiceImpl transportRecordService;
    @Resource
    private BusLineServiceImpl busLineService;
    @Resource
    private BusBaseInfoServiceImpl busBaseInfoService;

    private final Logger logger = LoggerFactory.getLogger(BanjiServiceImpl.class);
    public Result readFromExcel(@RequestParam String fileName ) {
        List<BanjiExcel> list =   new ArrayList<BanjiExcel>();
        BanjiExcel banjiExcel = null;
        Banji banji = null;

        File file =  new File(fileName);
        try {

            InputStream is = new FileInputStream(file);
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
            HSSFSheet hssfSheet = hssfWorkbook.getSheet("班级信息");

            if (hssfSheet == null) {
                return ResultGenerator.genFailResult("No 班级信息 sheet found");
            }
            // 循环行Row
            for (int rowNum = 0; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                HSSFRow hssfRow = hssfSheet.getRow(rowNum);
                if (hssfRow != null) {
                    banjiExcel = new BanjiExcel();
                    banji = new Banji();
                    HSSFCell nianjiCell = hssfRow.getCell(1);
                    HSSFCell banjiCell = hssfRow.getCell(2);
                    HSSFCell bzrNameCell = hssfRow.getCell(3);
                    HSSFCell bzrCodeCell = hssfRow.getCell(4);
                    banjiExcel.setGrade(CommonService.getValue(nianjiCell));
                    banjiExcel.setClassName(CommonService.getValue(banjiCell));
                    banjiExcel.setChargeTeacherName(CommonService.getValue(bzrNameCell));
                    banjiExcel.setChargeTeacherCode(CommonService.getValue(bzrCodeCell));
                    list.add(banjiExcel);

                    banji.setGrade(banjiExcel.getGrade());
                    banji.setClassName(banjiExcel.getClassName());
                    /**
                     * 如果有对应的user教师则保存
                     */
                    User bzr = userService.findBy("name", banjiExcel.getChargeTeacherName());
                    if (null != bzr) {
                        banji.setChargeTeacher(bzr.getId());
                    }
                    /**
                     * 班级目前不存在则增加
                     * 注意这里不能用findby(filedName ,...)
                     */
                    Class cl = Class.forName("com.eservice.api.model.banji.Banji");
                    Field fieldClassName = cl.getDeclaredField("className");//成员名
                    Banji banjiExist = null;
                    // 注意, 多次导入，目前不会导致班级重复，但是会出错。清一次班级数据然后重新导即可（因为班级家名字重复）
                    banjiExist = banjiService.findBy(fieldClassName.getName(), banjiExcel.getClassName());
                    /**
                     * 班级名称不存在，则增加
                     */
                    if ((null == banjiExist)) {
                        banji.setCreateTime(new Date());
                        banjiService.save(banji);
                        logger.info("add: =====" + rowNum + ":" + banjiExcel.getGrade() + "/"
                                + banjiExcel.getClassName() + "/"
                                + banjiExcel.getChargeTeacherName());
                    } else if(banjiExist != null) {
                        if ((banjiExist.getGrade() != banji.getGrade())) {
                            /**
                             * 虽然班级名称存在，但是年级不同，也增加
                             */
                            banji.setCreateTime(new Date());
                            banjiService.save(banji);
                            logger.info("add: =====" + rowNum + ":" + banjiExcel.getGrade() + "/"
                                    + banjiExcel.getClassName() + "/"
                                    + banjiExcel.getChargeTeacherName());
                        } else {
                            /**
                             * 班级名称存在，年级也相同，则更新
                             */
                            banji.setUpdateTime(new Date());
                            banji.setId(banjiExist.getId());
                            banjiService.update(banji);
                            logger.info("Update: =====" + rowNum + ":" + banjiExcel.getGrade() + "/"
                                    + banjiExcel.getClassName() + "/"
                                    + banjiExcel.getChargeTeacherName());
                        }
                    }

                }
            }

        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    public String getAllAbsenceToday( ) {
        String allMessage = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String queryStartTime = sdf.format(new Date());
        String queryFinishTime = null;

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DAY_OF_MONTH, 1);
        Date tomorrow = c.getTime();
        queryFinishTime = sdf.format(tomorrow);

        List<Banji> banjiList = banjiService.findAll();;
        List<StudentInfo> absenceStudentInfoList;
        String strTmp = null;
        for (int i = 0; i <banjiList.size() ; i++) {
            absenceStudentInfoList = transportRecordService.selectAbsenceStudentInfo( queryStartTime, queryFinishTime,
                    null, Constant.BUS_MODE_MORNING, null, banjiList.get(i).getClassName());
//            logger.info(banjiList.get(i).getClassName() + " 缺乘人数： " + absenceStudentInfoList.size());

            for (int j = 0; j <absenceStudentInfoList.size() ; j++) {
                if (strTmp == null) {
                    strTmp = absenceStudentInfoList.get(j).getName();
                } else {
                    strTmp = strTmp + ";" + absenceStudentInfoList.get(j).getName();
                }
            }

            String messageOfSMS = "  班级: " + banjiList.get(i).getClassName() + " 缺乘人数： " + absenceStudentInfoList.size() + strTmp;
            logger.info(messageOfSMS);
            allMessage = allMessage + messageOfSMS + "\r\n";
        }
        return allMessage;

    }

    public String getAbsenceTodayByGradeClass(String gradeName, String banjiName,String busMode) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String queryStartTime = sdf.format(new Date());
        String queryFinishTime = null;

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DAY_OF_MONTH, 1);
        Date tomorrow = c.getTime();
        queryFinishTime = sdf.format(tomorrow);

        BanjiInfo bzr = banjiService.getTheChargeTeacher(gradeName, banjiName);
        if (bzr == null) {
            logger.error(" could not get the charge teacher for grade: " + gradeName + " banji: " + banjiName);
        }

        List<StudentInfo> absenceStudentInfoList;
        String strTmp = null;
        absenceStudentInfoList = transportRecordService.selectAbsenceStudentInfo(queryStartTime, queryFinishTime,
                null, busMode, gradeName, banjiName);

        for (int j = 0; j < absenceStudentInfoList.size(); j++) {
            if (strTmp == null) {
                strTmp = absenceStudentInfoList.get(j).getName()
                        + "(校车：" + absenceStudentInfoList.get(j).getBusNumber() + ")";
            } else {
                strTmp = strTmp + ";" + absenceStudentInfoList.get(j).getName()
                        + "(校车：" + absenceStudentInfoList.get(j).getBusNumber() + ")";
            }
        }

        String messageOfSMS = null;
        Date now = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if (absenceStudentInfoList.size() > 0) {
            messageOfSMS = bzr.getName() + "老师，截止" + formatter.format(now) + "，你班搭乘校车的同学中"
                    + "，缺乘总人数： " + absenceStudentInfoList.size() + "，具体名单：" + strTmp;
        } else {
            messageOfSMS = bzr.getName() + "老师，截止" + formatter.format(now) + "你班学生来校校车乘坐无异常";
        }
        logger.info(messageOfSMS);
        return messageOfSMS;

    }

    public List<User> getChargeTeachers() {
        return banjiMapper.getChargeTeachers();
    }

    public List<User> get1To8GradeChargeTeachers() {
        return banjiMapper.get1To8GradeChargeTeachers();
    }

    public BanjiInfo getTheChargeTeacher(String gradeName, String banjiName) {
        return banjiMapper.getTheChargeTeacher(gradeName,banjiName);
    }

    public List<Banji> getBanjiListByGrade(String gradeName) {
        return banjiMapper.getBanjiListByGrade(gradeName);
    }

    public List<Banji> getBanji1to8List() {
        return banjiMapper.getBanji1to8List();
    }

    public boolean isBanjiExist(String gradeName,String banjiName){
        if( banjiMapper.isBanjiExist(gradeName,banjiName).isEmpty()){
            return false;
        } else {
            return true;
        }
    }
	
    public Banji getBanjiByGradeNameAndBanjiName(String gradeName,String banjiName) {
        return banjiMapper.getBanjiByGradeNameAndBanjiName(gradeName, banjiName);
    }
	
    public BanjiInfo getBanjiInfoByBzr(String bzrAccount) {
        return banjiMapper.getBanjiInfoByBzr(bzrAccount);
    }

    public List<Banji> listByClassName(){
        return banjiMapper.listByClassName();
    }

    /**
     * 从URL里创建班级
     * @param urlStr
     * @return 返回创建的班级数量
     */
    public Integer getURLContentAndCreateBanji(String urlStr) {
        Integer addedBanjiSum = 0;
        Integer updatedBanjiSum = 0;
        String strFromUrl = CommonService.getUrlResponse(urlStr);
        try {
            JSONObject jsonObject = JSON.parseObject(strFromUrl);
            JSONArray ja = jsonObject.getJSONArray("result");
            logger.info(" banji fake sum: " + ja.size());
            for (int i = 0; i < ja.size(); i++) {
                Banji banji = new Banji();
                JSONObject jo = ja.getJSONObject(i);
                String grade = jo.getString("grade");
                String banjiName = jo.getString("name");
                String teacher_name = jo.getString("teacher_name");
                String classId = jo.getString("id");
                banji.setGrade(grade);
                banji.setClassName(banjiName);
                banji.setCreateTime(new Date());
                banji.setClassIdFromUrl(classId);

                Class cl = Class.forName("com.eservice.api.model.user.User");
                Field fieldUserAccount = cl.getDeclaredField("account");
                User userExist = null;
                userExist = userService.findBy(fieldUserAccount.getName(), teacher_name);
                if (userExist == null) {
                    logger.warn("Can not find the user by account " + teacher_name);
                } else {
                    banji.setChargeTeacher(userExist.getId());
                }

                /**
                 * 班级不存在时，更新班级
                 * 班级不存在时，增加班级
                 */
                Banji banjiExist = banjiService.getBanjiByGradeNameAndBanjiName(grade, banjiName);
                if ( banjiExist != null) {
                    logger.info(" already exist banji: " + grade + "," + banjiName);
                    banjiExist.setGrade(grade);
                    banjiExist.setClassName(banjiName);
                    banjiExist.setCreateTime(new Date());
                    banjiExist.setClassIdFromUrl(classId);
                    if (userExist == null) {
                        logger.warn("Can not find the user by account " + teacher_name);
                    } else {
                        banjiExist.setChargeTeacher(userExist.getId());
                    }
                    banjiService.update(banjiExist);
                    updatedBanjiSum ++;
                    logger.info("update banji: " + banjiExist.getGrade() + "," + banjiExist.getClassName());
                } else {
                    banjiService.save(banji);
                    logger.info("Add banji: " + banji.getGrade() + "," + banji.getClassName());
                    addedBanjiSum++;
                }
            }

        } catch (Exception e) {
            logger.warn(" exception: " + e.toString());
        }
        logger.info( "updated: " + updatedBanjiSum + ", added: " + addedBanjiSum);
        return addedBanjiSum;
    }
}
