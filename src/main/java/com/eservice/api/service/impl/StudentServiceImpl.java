package com.eservice.api.service.impl;

import com.eservice.api.core.Result;
import com.eservice.api.core.ResultGenerator;
import com.eservice.api.dao.StudentMapper;
import com.eservice.api.model.banji.Banji;
import com.eservice.api.model.banji.BanjiExcel;
import com.eservice.api.model.student.Student;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
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

    private final Logger logger = LoggerFactory.getLogger(StudentServiceImpl.class);
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

    public StudentInfo getSutdentInfo(String studentNumber){
        return studentMapper.getSutdentInfo(studentNumber);
    }

    /**
     * @param busNumber
     * @param busMode 早班午班每个学生都是同辆车，这个参数传不传都一样, 都返回相同的学生
     * @param busStation
     */
    public List<StudentInfo> getPlannedStudents(String busNumber, String busMode,String busStation){
        if(busMode == null){
            return studentMapper.getPlannedStudents(busNumber,busStation);
        } else if(busMode.equals(Constant.BUS_MODE_MORNING)){
            return studentMapper.getPlannedStudentsMorning(busNumber,busStation);
        } else if(busMode.equals(Constant.BUS_MODE_AFTERNOON)){
            return studentMapper.getPlannedStudentsAfternoon(busNumber,busStation);
        } else {
            return null;
        }

    }

    public List<StudentInfo> getStudents(String className) {
        return studentMapper.getStudents(className);
    }

    public Result readFromExcel(@RequestParam String fileName ) {
        List<StudentExcel> list =   new ArrayList<StudentExcel>();
        StudentExcel studentExcel = null;
        Student student = null;
        int sumOfAddInSheet1 = 0;
        int sumOfAddInSheet2 = 0;

        File file =  new File(fileName);
        try {

            InputStream is = new FileInputStream(file);
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
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
                    HSSFCell studentNumberCell = hssfRow.getCell(1);
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
                    // todo 班级名称重复时
                    banjiExist = banjiService.findBy(fieldClassName.getName(), studentExcel.getBanjiName());
                    if(null != banjiExist){
                        student.setBanji(banjiExist.getId());
                    }
                    student.setName(studentExcel.getName());
                    student.setStudentNumber(studentExcel.getStudentNumber().split("\\.")[0]);
                    list.add(studentExcel);

                    Student studentExist = null;
                    Class cl2 = Class.forName("com.eservice.api.model.student.Student");
                    Field fieldClassName2 = cl2.getDeclaredField("studentNumber");
                    studentExist = studentService.findBy(fieldClassName2.getName(), student.getStudentNumber());

                    /**
                     * 学生学号不存在，则增加, 存在则更新
                     */
                    if ((null == studentExist)) {
                        studentService.save(student);
                        sumOfAddInSheet1++;
                        logger.info("added : =====" + student.getName() + "/" + student.getStudentNumber() + ", sum is " + sumOfAddInSheet1);
                    } else {
                        student.setId(studentExist.getId());
                        studentService.update(student);
                        logger.info("updated : =====" + student.getName() + "/" + student.getStudentNumber() );
                    }
                }
            }

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
                    HSSFCell studentNumberCell = hssfRow.getCell(1);
                    HSSFCell studentNameCell = hssfRow.getCell(3);
                    HSSFCell studentBanjiCell = hssfRow.getCell(2);
                    HSSFCell studentFamilyCell = hssfRow.getCell(17);

                    studentExcel.setStudentNumber(CommonService.getValue(studentNumberCell));
                    studentExcel.setName(CommonService.getValue(studentNameCell));
                    if(null != studentBanjiCell) {
                        studentExcel.setBanjiName(CommonService.getValue(studentBanjiCell));
                    }
                    if(null != studentFamilyCell) {
                        studentExcel.setFamilyInfo(CommonService.getValue(studentFamilyCell));
                    }/**
                     * 查找班级
                     */
                    Banji banjiExist = null;
                    Class cl = Class.forName("com.eservice.api.model.banji.Banji");
                    Field fieldClassName = cl.getDeclaredField("className");

                    if(studentExcel.getBanjiName() != null) {
                        // todo 班级名称重复时
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
                        studentService.save(student);
                        sumOfAddInSheet2++;
                        logger.info("sheet2 added : =====" + student.getName() + "/" + student.getStudentNumber() + ", sum is " + sumOfAddInSheet2);
                    } else {
                        student.setId(studentExist.getId());
                        studentService.update(student);
                        logger.info("sheet2 updated : =====" + student.getName() + "/" + student.getStudentNumber() );
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
}
