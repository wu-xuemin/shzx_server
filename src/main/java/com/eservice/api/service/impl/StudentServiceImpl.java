package com.eservice.api.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eservice.api.core.Result;
import com.eservice.api.core.ResultGenerator;
import com.eservice.api.dao.StudentMapper;
import com.eservice.api.model.banji.Banji;
import com.eservice.api.model.banji.BanjiExcel;
import com.eservice.api.model.bus_line.BusLine;
import com.eservice.api.model.bus_line.BusLineExcelHelper;
import com.eservice.api.model.bus_stations.BusStations;
import com.eservice.api.model.student.Student;
import com.eservice.api.model.student.StudentBusInfo;
import com.eservice.api.model.student.StudentExcel;
import com.eservice.api.model.student.StudentInfo;
import com.eservice.api.model.user.User;
import com.eservice.api.service.StudentService;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.io.*;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
* Class Description: xxx
* @author Wilson Hu
* @date 2018/12/24.
*/
@Service
@Transactional
public class StudentServiceImpl extends AbstractService<Student> implements StudentService {
    @Resource
    private StudentMapper studentMapper;

    @Resource
    private BanjiServiceImpl banjiService;

    @Resource
    private StudentServiceImpl studentService;

    @Resource
    private BusLineServiceImpl busLineService;

    @Resource
    private BusStationsServiceImpl busStationsService;

    private final Logger logger = LoggerFactory.getLogger(StudentServiceImpl.class);

    @Value("${student_img_dir}")
    private String STUDENT_IMG_DIR;

    /**
     * 以姓名命名的照片 所在的路径
     */
    @Value("${student_img_dir_name}")
    private String STUDENT_IMG_DIR_NAME;

    /**
     * 以学号命名的照片 所在的路径
     */
    @Value("${student_img_dir_number}")
    private String STUDENT_IMG_DIR_NUMBER;

    @Value("${url_style}")
    private String urlStyle;

    @Value("${student_img_url_prefix}")
    private String studentImgUrlPrefix;

    /**
     * 这个函数可以用后面的 getPlannedStudents 替代。
     */
    public List<StudentInfo> getPlannedStudentsByBusNumberAndBusMode(String busNumber, String busMode) {
        if(busMode.equals(Constant.BUS_MODE_MORNING)){
            return studentMapper.getPlannedStudentsMorningByBusNumber(busNumber);
        } else if(busMode.equals(Constant.BUS_MODE_AFTERNOON)){
            return studentMapper.getPlannedStudentsAfternoonByBusNumber(busNumber);
        } else {
            return null;
        }
    }

    public StudentInfo getStudentInfo(String studentNumber){
        return studentMapper.getStudentInfo(studentNumber);
    }

    /**
     * @param busNumber
     * @param busMode 上学放学每个学生都是同辆车，这个参数传不传都一样, 都返回相同的学生
     * @param busStation
     */
    public List<StudentInfo> getPlannedStudents(String busNumber, String busMode,String busStation,
                                                String gradeName, String className){
        if(busMode == null){
            return studentMapper.getPlannedStudents(busNumber,busStation,gradeName,className);
        } else if(busMode.equals(Constant.BUS_MODE_MORNING)){
            return studentMapper.getPlannedStudentsMorning(busNumber,busStation,gradeName,className);
        } else if(busMode.equals(Constant.BUS_MODE_AFTERNOON)){
            return studentMapper.getPlannedStudentsAfternoon(busNumber,busStation,gradeName,className);
        } else {
            return null;
        }

    }

    public List<StudentInfo> getStudents(String gradeName,String className, String queryKey) {
        return studentMapper.getStudents(gradeName,className,queryKey);
    }

    /**
     *从xls excel里读取学生信息(不包括线路和上下车站点),注意有两个sheet,Sheet2 包含了所有年级，sheet1只包含1-12年级
     */
    public Result readFromExcel(@RequestParam String fileName ) {
        List<StudentExcel> list =   new ArrayList<StudentExcel>();
        StudentExcel studentExcel = null;
        Student student = null;
        int sumOfAddInSheet1 = 0;
        int sumOfAddInSheet2 = 0;
        DecimalFormat format = new DecimalFormat("#");

        File file =  new File(fileName);
        try {

            InputStream is = new FileInputStream(file);
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);


            /////////////////// 根据 sheet2
            HSSFSheet hssfSheet2 = hssfWorkbook.getSheet("Sheet2");

            if (hssfSheet2 == null) {
                return ResultGenerator.genFailResult("No Sheet2 sheet found");
            }
            // 循环行Row
            for (int rowNum = 1; rowNum <= hssfSheet2.getLastRowNum(); rowNum++) {
                HSSFRow hssfRow = hssfSheet2.getRow(rowNum);
                if (hssfRow != null) {
                    studentExcel = new StudentExcel();
                    student = new Student();
                    HSSFCell studentNumberCell = hssfRow.getCell(0);
                    HSSFCell studentNameCell = hssfRow.getCell(3);
                    HSSFCell studentBanjiCell = hssfRow.getCell(2);
                    // 监护人姓名
                    HSSFCell studentGuardianNameCell = hssfRow.getCell(17);
                    // 监护人手机
                    HSSFCell studentGuardianPhoneCell = hssfRow.getCell(19);

                    studentExcel.setStudentNumber(CommonService.getValue(studentNumberCell));
                    studentExcel.setName(CommonService.getValue(studentNameCell));
                    if(null != studentBanjiCell) {
                        studentExcel.setBanjiName(CommonService.getValue(studentBanjiCell));
                    }

                    /**
                     * 监护人及其电话，excel格式混杂
                     */
                    JSONObject jsonObject = new JSONObject();
                    if(null != studentGuardianNameCell) {
                        jsonObject.put("监护人", CommonService.getValue(studentGuardianNameCell));
                        if(null != studentGuardianPhoneCell) {
                            jsonObject.put("联系方式", format.format(studentGuardianPhoneCell.getNumericCellValue()));
//                            jsonObject.put("联系方式", CommonService.getValue(studentGuardianPhoneCell));
                        }
                    }
                    studentExcel.setFamilyInfo(jsonObject.toJSONString());
                    /**
                     * 查找班级
                     */
                    Banji banjiExist = null;
                    Class cl = Class.forName("com.eservice.api.model.banji.Banji");
                    Field fieldClassName = cl.getDeclaredField("className");

                    if(studentExcel.getBanjiName() != null) {
                        banjiExist = banjiService.findBy(fieldClassName.getName(), studentExcel.getBanjiName());
                    }
                    if(null != banjiExist){
                        student.setBanji(banjiExist.getId());
                    }
                    student.setName(studentExcel.getName());
                    student.setStudentNumber(studentExcel.getStudentNumber().split("\\.")[0]);
                    student.setFamilyInfo(studentExcel.getFamilyInfo());
                    list.add(studentExcel);

                    Student studentExist = null;
                    Class cl2 = Class.forName("com.eservice.api.model.student.Student");
                    Field fieldClassName2 = cl2.getDeclaredField("studentNumber");
                    studentExist = studentService.findBy(fieldClassName2.getName(), student.getStudentNumber());

                    /**
                     * 学生学号不存在，则增加, 存在则更新
                     */
                    if ((null == studentExist)) {
                        student.setCreateTime(new Date());
                        studentService.save(student);
                        sumOfAddInSheet2++;
                        logger.info("sheet2 added : =====" + student.getName() + "/" + student.getStudentNumber() + ", sum is " + sumOfAddInSheet2);
                    } else {
                        student.setUpdateTime(new Date());
                        student.setId(studentExist.getId());
                        studentService.update(student);
                        logger.info("sheet2 updated : =====" + student.getName() + "/" + student.getStudentNumber() );
                    }
                }
            }

            HSSFSheet hssfSheet = hssfWorkbook.getSheet("1-12年级学生信息");

            if (hssfSheet == null) {
                return ResultGenerator.genFailResult("No 1-12年级学生信息 sheet found");
            }
            // 循环行Row
            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                HSSFRow hssfRow = hssfSheet.getRow(rowNum);
                if (hssfRow != null) {
                    studentExcel = new StudentExcel();
                    student = new Student();
                    HSSFCell studentNumberCell = hssfRow.getCell(0);
                    HSSFCell studentNameCell = hssfRow.getCell(2);
                    HSSFCell studentGradeCell = hssfRow.getCell(6);
                    HSSFCell studentBanjiCell = hssfRow.getCell(7);
                    HSSFCell studentBzrNameCell = hssfRow.getCell(16);

                    studentExcel.setStudentNumber(CommonService.getValue(studentNumberCell));
                    studentExcel.setName(CommonService.getValue(studentNameCell));
                    studentExcel.setBanjiName(CommonService.getValue(studentBanjiCell));
                    studentExcel.setGradeName(CommonService.getValue(studentGradeCell));
                    studentExcel.setBzrName(CommonService.getValue(studentBzrNameCell));

                    /**
                     * 查找班级
                     */
                    Banji banjiExist = null;
                    Class cl = Class.forName("com.eservice.api.model.banji.Banji");
                    Field fieldClassName = cl.getDeclaredField("className");
                    banjiExist = banjiService.findBy(fieldClassName.getName(), studentExcel.getBanjiName());
                    if(null != banjiExist){
                        student.setBanji(banjiExist.getId());
                    }
                    student.setName(studentExcel.getName());
                    student.setStudentNumber(studentExcel.getStudentNumber().split("\\.")[0]);
                    student.setValid(Constant.VALID_YES);
                    list.add(studentExcel);

                    Student studentExist = null;
                    Class cl2 = Class.forName("com.eservice.api.model.student.Student");
                    Field fieldClassName2 = cl2.getDeclaredField("studentNumber");
                    studentExist = studentService.findBy(fieldClassName2.getName(), student.getStudentNumber());

                    /**
                     * 学生学号不存在，则增加, 存在则更新
                     */
                    if ((null == studentExist)) {
                        student.setCreateTime(new Date());
                        studentService.save(student);
                        sumOfAddInSheet1++;
                        logger.info("sheet 1 added : =====" + student.getName() + "/" + student.getStudentNumber() + ", sum is " + sumOfAddInSheet1);
                    } else {
                        student.setUpdateTime(new Date());
                        student.setId(studentExist.getId());
                        studentService.update(student);
                        logger.info("sheet1 updated : =====" + student.getName() + "/" + student.getStudentNumber() );
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

    /**
     * 更新每个学生的校车线路和上下车站点信息
     * NOTE: 在生成学生之后再调用该接口，该接口不生成学生，只是更新学生的线路和站点信息。
     */
    public Result parseInfoFromExcelForBoardStation(@RequestParam String fileName ) {
        List<BusLineExcelHelper> list =   new ArrayList<BusLineExcelHelper>();
        BusLineExcelHelper busLineExcelHelper = null;
        Student student = null;
        int sumOfUpdatedInSheet1 = 0;

        File file =  new File(fileName);
        try {

            InputStream is = new FileInputStream(file);
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
            HSSFSheet hssfSheet = hssfWorkbook.getSheet("Sheet1");

            if (hssfSheet == null) {
                return ResultGenerator.genFailResult("No Sheet1 sheet found");
            }
            // 循环行Row
            for (int rowNum = 2; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                HSSFRow hssfRow = hssfSheet.getRow(rowNum);
                if (hssfRow != null) {
                    busLineExcelHelper = new BusLineExcelHelper();
                    student = new Student();
                    HSSFCell busNumberCell = hssfRow.getCell(0);
                    HSSFCell stationTimeRemarkCell = hssfRow.getCell(14);
                    HSSFCell stationNameCell = hssfRow.getCell(2);
                    HSSFCell studentNumberCell = hssfRow.getCell(3);
                    HSSFCell studentNameCell = hssfRow.getCell(4);
                    busLineExcelHelper.setBusNumber(CommonService.getValue(busNumberCell));
                    busLineExcelHelper.setStationName(CommonService.getValue(stationNameCell));
                    busLineExcelHelper.setStudentNumber(CommonService.getValue(studentNumberCell).trim());
                    busLineExcelHelper.setStudentName(CommonService.getValue(studentNameCell));

                    /**
                     * 查找学生
                     */
                    Student studentExist = null;
                    Class cl = Class.forName("com.eservice.api.model.student.Student");
                    Field fieldStudentNumber = cl.getDeclaredField("studentNumber");
                    studentExist = studentService.findBy(fieldStudentNumber.getName(), busLineExcelHelper.getStudentNumber().split("\\.")[0]);
                    if (null == studentExist) {
//                        return ResultGenerator.genFailResult("No student found by number: " + busLineExcelHelper.getStudentNumber());
                       logger.warn("No student found by number: " + busLineExcelHelper.getStudentNumber());
                       continue;
                    }
                    /**
                     * 查找线路
                     */
                    BusLine busLineExist = null;
                    Class classBusLine = Class.forName("com.eservice.api.model.bus_line.BusLine");
                    Field fieldBusLineName = classBusLine.getDeclaredField("name");
                    busLineExist = busLineService.findBy(fieldBusLineName.getName(),
                            busLineExcelHelper.getBusNumber().split("\\.")[0] + "号车_" + CommonService.getBusModeByTime(stationTimeRemarkCell));
                    if (busLineExist != null) {
                        student.setBusLineMorning(busLineExist.getId());
                        // 放学和上学不同。目前表格里只有上学
                        BusLine busLineExistWuban = null;
                        busLineExistWuban = busLineService.findBy(fieldBusLineName.getName(),
                                busLineExcelHelper.getBusNumber().split("\\.")[0] + "号车_放学" );
                        student.setBusLineAfternoon(busLineExistWuban.getId());
                    }

                    /**
                     * 查找站点
                     */
                    BusStations busStationsExist = null;
                    Class classBusStation = Class.forName("com.eservice.api.model.bus_stations.BusStations");
                    Field fieldStationName = classBusStation.getDeclaredField("stationName");
                    busStationsExist = busStationsService.findBy(fieldStationName.getName(), busLineExcelHelper.getStationName());
                    if (busStationsExist != null) {
                        student.setBoardStationMorning(busStationsExist.getId());
                        student.setBoardStationAfternoon(busStationsExist.getId());
                    }
                    list.add(busLineExcelHelper);

                    /**
                     * 线路和站点，更新
                     */
                    student.setUpdateTime(new Date());
                    student.setId(studentExist.getId());
                    studentService.update(student);
                    sumOfUpdatedInSheet1 ++;
                    if((busLineExist != null) && busStationsExist != null ) {
                        logger.info("updated=====" + sumOfUpdatedInSheet1 + " : "+ busLineExcelHelper.getStudentName() + "/" + busLineExcelHelper.getStudentNumber().split("\\.")[0]
                                + "==>线路：" + busLineExist.getName()
                                + " 站点：" + student.getBoardStationMorning() + "(" + busStationsExist.getStationName() + ")");
                    } else {
                        logger.info("updated=====" + " : " + busLineExcelHelper.getStudentName() + "/" + busLineExcelHelper.getStudentNumber()
                                + " without busLine/busStation");
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

    /**
     * 返回学生照片文件存在，但是在数据库中不存在的照片文件名
     *  12345_张三.jpg
     */
    public List<String> getAndInsertStudentHeadImg() {
        File dir = new File(STUDENT_IMG_DIR);
        if(!dir.exists()) {
            dir.mkdirs();
        }
        List<String> list = new ArrayList<String>();
        File file = new File(STUDENT_IMG_DIR);
        File[] tempList = file.listFiles();

        Student student = null;
        Integer count = 0;
        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                /*
                 *根据学号查学生
                 */
                student = studentService.getStudentInfo(tempList[i].getName().split("_")[0]);
                if(student != null) {
                    if(urlStyle.equals(Constant.URL_PATH_STYLE_RELATIVE)) {
                        student.setHeadImg( tempList[i].getName().split("_")[0] + "_"  + student.getName() + "." + tempList[i].getName().split("\\.")[1]);
                    } else {
                        student.setHeadImg(studentImgUrlPrefix  + tempList[i].getName().split("_")[0] + "_"  + student.getName() + "." + tempList[i].getName().split("\\.")[1]);
                    }
                    student.setUpdateTime(new Date());
                    studentService.update(student);
                    logger.info("学生：" + tempList[i].getName().split("_")[0] + " 已更新head_img");
                } else {
                    count ++;
                    logger.warn("根据文件 " + tempList[i].getName() + "，找不到对应的学生, " + count);
                    list.add(tempList[i].getName());
                }
            }
        }
        return list;
    }

    /**
     *  改名： 2345.xxx --> 2344_张三.xxx
     */
    public List<String> renameNumberStudentPicFile() {
        File dir = new File(STUDENT_IMG_DIR_NUMBER);
        if(!dir.exists()) {
            dir.mkdirs();
        }
        List<String> list = new ArrayList<String>();
        File file = new File(STUDENT_IMG_DIR_NUMBER);
        File[] tempList = file.listFiles();

        Student student = null;
        Integer count = 0;
        Integer countRenamed = 0;
        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                student = studentService.getStudentInfo(tempList[i].getName().split("\\.")[0]);
                if(student != null) {
                    /**
                     * 改名： 2345.xxx --> 2344_张三.xxx
                     */
                    String newName = tempList[i].getName().split("\\.")[0] + "_"
                            + student.getName()
                            + "." + tempList[i].getName().split("\\.")[1];
                    CommonService.renameFile(STUDENT_IMG_DIR_NUMBER,tempList[i].getName(),newName);
                    countRenamed ++;

                } else {
                    count ++;
                    logger.warn("根据文件 学号 " + tempList[i].getName() + "，找不到对应的学生, " + count);
                    list.add(tempList[i].getName());
                }
            }
        }
        logger.info("countRenamed " + countRenamed);
        return list;
    }

    /**
     * 改名： 张三.xxx --> 2344_张三.xxx
     */
    public List<String> renameNameStudentPicFile() {
        File dir = new File(STUDENT_IMG_DIR_NAME);
        if(!dir.exists()) {
            dir.mkdirs();
        }
        List<String> list = new ArrayList<String>();
        File file = new File(STUDENT_IMG_DIR_NAME);
        File[] tempList = file.listFiles();

        Integer countNotFound = 0;
        Integer countRenamed = 0;
        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                /**
                 * 根据姓名查找，可能有重复
                 */
                List<StudentInfo> studentList = studentService.getStudents(null,null,tempList[i].getName().split("\\.")[0]);
                if(studentList.isEmpty()){
                    countNotFound ++;
                    logger.warn("根据文件 " + tempList[i].getName() + "，找不到对应的学生, " + countNotFound);
                    list.add(tempList[i].getName());
                } else if(studentList.size() ==1) {
                    /**
                     * 改名： 张三.xxx --> 2344_张三.xxx
                     */
                    String newName = studentList.get(0).getStudentNumber()
                            + "_" + tempList[i].getName().split("\\.")[0]
                            + "." + tempList[i].getName().split("\\.")[1];
                    CommonService.renameFile(STUDENT_IMG_DIR_NAME,tempList[i].getName(),newName);
                    countRenamed ++;
                    logger.info("重命名OK: " + tempList[i].getName() + "==>" + newName + " :" + countRenamed);

                } else {
                    logger.warn("根据文件 姓名 " + tempList[i].getName() + "，找到多个同名的学生, " + studentList.size());
                }
 
            }
        }
        logger.info("countRenamed " + countRenamed);
        return list;
    }

    public List<StudentBusInfo> getPlannedClassStudent(String queryKey,String className,String gradeName){
        List<StudentInfo> info=  studentMapper.getPlannedClassStudents(gradeName,className,queryKey);
        List<StudentBusInfo> infos=new ArrayList<>();
        for (StudentInfo studentInfo:info){
            StudentBusInfo studentBusInfo=new StudentBusInfo();
            studentBusInfo.setBanjiName(studentInfo.getBanjiName());
            studentBusInfo.setBanji(studentInfo.getBanji());
            studentBusInfo.setBusNumber(studentInfo.getBusNumber());
            studentBusInfo.setName(studentInfo.getName());
            studentBusInfo.setHeadImg(studentInfo.getHeadImg());
            studentBusInfo.setStudentNumber(studentInfo.getStudentNumber());
            studentBusInfo.setBanjiName(studentInfo.getBanjiName());
            infos.add(studentBusInfo);
        }
        return infos;
    }

    public Integer deleteStudentsNotRideSchoolBus(){
        return studentMapper.deleteStudentsNotRideSchoolBus();
    }

    public String getURLContentAndCreateStu(String urlStrStudent, String urlStrBus){
        Integer addedStuSum = 0;
        Integer noRideBusStuSum =0;
        String strFromUrl = CommonService.getUrlResponse(urlStrStudent);
        try {
            JSONObject jsonObject= JSON.parseObject(strFromUrl);
            JSONArray ja = jsonObject.getJSONArray("result");
            for (int i = 0; i < ja.size(); i++) {
                Student student = new Student();
                JSONObject jo = ja.getJSONObject(i);
                String classId = jo.getString("class_id");
                String stuName = jo.getString("name");
                String stuNumber = jo.getString("student_number");

                student.setName(stuName);
                student.setStudentNumber(stuNumber);
                student.setCreateTime(new Date());
                student.setValid(Constant.VALID_YES);

                Class cl = Class.forName("com.eservice.api.model.banji.Banji");
                Field field = cl.getDeclaredField("classIdFromUrl");
                Banji banjiExist = banjiService.findBy(field.getName(), classId);
                if(banjiExist == null) {
                    logger.warn(" can not find banji by classId: " + classId);
                } else {
                    student.setBanji(banjiExist.getId());
                }

                Class cl2 = Class.forName("com.eservice.api.model.student.Student");
                Field fieldStuNum = cl2.getDeclaredField("studentNumber");
                Student studentExist = studentService.findBy(fieldStuNum.getName(), stuNumber);
                if(studentExist == null) {
                    studentService.save(student);
                    logger.info("added student: " + student.getName());
                    addedStuSum ++;
                } else {
                    logger.info(" already exist student: " +  student.getName());
                }
            }

        } catch (Exception e) {
            logger.warn(" exception: " + e.getMessage());
            return (" exception: " + e.getMessage());
        }
        /**
         * 获取学生的校车编号，站点，电话信息（放到family字段）
         */
        String strFromUrlBus = CommonService.getUrlResponse(urlStrBus);
        try {
            JSONObject jsonObject= JSON.parseObject(strFromUrlBus);
            JSONArray ja = jsonObject.getJSONArray("result");
            for (int i = 0; i < ja.size(); i++) {
                JSONObject jo = ja.getJSONObject(i);
                Student studentInBusUrl = new Student();
                String stuNumber = jo.getString("student_number");
                String busNumber = jo.getString("id");
                String stationName = jo.getString("station_name");
                String phone = jo.getString("phone");

                studentInBusUrl = studentService.getStudentInfo(stuNumber);
                if(studentInBusUrl == null){
                    logger.warn("Can not find student by studentNumber " + stuNumber);
                } else{
                    /**
                     * 班次编号
                     */
                    BusLine busLineExist = busLineService.findBy("name", busNumber + "号车_上学" );

                    if (busLineExist == null) {
                        logger.warn("Can not find busLine by bus number " + busNumber);
                    } else {
                        studentInBusUrl.setBusLineMorning(busLineExist.getId());
                        BusLine busLineExistWuban = null;
                        busLineExistWuban = busLineService.findBy("name", busNumber + "号车_放学" );
                        studentInBusUrl.setBusLineAfternoon(busLineExistWuban.getId());
                    }
                    /**
                     * 站点
                     */
                    BusStations busStation = busStationsService.getBusStation(stationName);
                    if(busStation == null){
                        logger.warn("Can not find station by stationName " + stationName);
                    } else {
                        studentInBusUrl.setBoardStationMorning(busStation.getId());
                        studentInBusUrl.setBoardStationAfternoon(busStation.getId());
                    }
                    /**
                     * 电话信息（放到family字段）
                     * TODO: URL ready后改为JSON格式
                     */
                    studentInBusUrl.setFamilyInfo(phone);
                    studentService.update(studentInBusUrl);
                }
            }

            /**
             * 删除不坐班车的学生
             */
            noRideBusStuSum = studentService.deleteStudentsNotRideSchoolBus();
            logger.info( noRideBusStuSum + " student(s) not riding school bus deleted");

        } catch (Exception e) {
            logger.warn(" exception: " + e.getMessage());
            return (" exception: " + e.getMessage());
        }
        return " Finally, " + (addedStuSum - noRideBusStuSum) + " student(s) added";
    }

    public List<Student> checkStudentImg(){
        List<Student>  list = studentMapper.checkStudentImg();
        return list;
    }

    public List<Student> checkStudentBus(){
        List<Student>  list = studentMapper.checkStudentBus();
        return list;
    }
}
