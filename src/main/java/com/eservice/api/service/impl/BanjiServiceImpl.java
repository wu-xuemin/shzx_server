package com.eservice.api.service.impl;

import com.eservice.api.core.Result;
import com.eservice.api.core.ResultGenerator;
import com.eservice.api.dao.BanjiMapper;
import com.eservice.api.model.banji.Banji;
import com.eservice.api.model.banji.BanjiExcel;
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
                    // todo, 多次导入，目前不会导致班级重复，但是会出错。清一次班级数据然后重新导即可（因为班级家名字重复）
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

    public String getAbsenceTodayByGradeClass(String gradeName, String banjiName ) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String queryStartTime = sdf.format(new Date());
        String queryFinishTime = null;

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DAY_OF_MONTH, 1);
        Date tomorrow = c.getTime();
        queryFinishTime = sdf.format(tomorrow);

        User bzr = banjiService.getTheChargeTeacher(gradeName,banjiName);
        if(bzr == null){
            logger.error(" could not get the charge teacher for grade: " + gradeName + " banji: " + banjiName);
        }

        List<StudentInfo> absenceStudentInfoList;
        String strTmp = null;
        absenceStudentInfoList = transportRecordService.selectAbsenceStudentInfo(queryStartTime, queryFinishTime,
                null, Constant.BUS_MODE_MORNING, gradeName, banjiName);

        for (int j = 0; j < absenceStudentInfoList.size(); j++) {
            if (strTmp == null) {
                strTmp = absenceStudentInfoList.get(j).getName()
                        + "(校车：" + absenceStudentInfoList.get(j).getBusNumber() + ")";
            } else {
                strTmp = strTmp + ";" + absenceStudentInfoList.get(j).getName()
                        + "(校车：" + absenceStudentInfoList.get(j).getBusNumber() + ")";
            }
        }
        /**
         *  1.**号张三，已上车未到校；2.**号车李四，未上校车。3、**号车王五，出现异常乘车情况。
         * todo ? 按校车分组缺乘学生
         */
//        String s = null;
//        List<BusBaseInfo> busAllList = busBaseInfoService.findAll();
//        for (int i = 0; i <busAllList.size() ; i++) {
//            for (int k = 0; k < absenceStudentInfoList.size(); k++) {
//                if(absenceStudentInfoList.get(k).getBusNumber().equals(busAllList.get(i).getNumber())){
//                    s = i +"号车"
//                }
//            }
//
//        }

        String messageOfSMS = null;
        Date now = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        messageOfSMS = bzr.getName() + "老师，截止" + formatter.format(now) + "，你班搭乘校车的同学中"
           +  "，缺乘总人数： " + absenceStudentInfoList.size() + "，具体名单：" + strTmp;

        logger.info(messageOfSMS);
        return messageOfSMS;

    }

    public List<User> getChargeTeachers() {
        return banjiMapper.getChargeTeachers();
    }

    public User getTheChargeTeacher(String gradeName, String banjiName) {
        return banjiMapper.getTheChargeTeacher(gradeName,banjiName);
    }

}
