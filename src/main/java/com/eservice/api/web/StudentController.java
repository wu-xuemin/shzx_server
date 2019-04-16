package com.eservice.api.web;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eservice.api.core.Result;
import com.eservice.api.core.ResultGenerator;
import com.eservice.api.model.banji.Banji;
import com.eservice.api.model.student.Student;
import com.eservice.api.model.student.StudentInfo;
import com.eservice.api.service.common.CommonService;
import com.eservice.api.service.common.Constant;
import com.eservice.api.service.impl.BanjiServiceImpl;
import com.eservice.api.service.impl.StudentServiceImpl;
import com.eservice.api.service.park.SyncStuService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

/**
* Class Description: xxx
* @author Wilson Hu
* @date 2018/12/24.
*/
@RestController
@RequestMapping("/student")
@Api(description = "学生信息管理")
public class StudentController {
    @Resource
    private StudentServiceImpl studentService;
    @Value("${student_img_dir}")
    private String studentImgDir;
    @Value("${url_style}")
    private String urlStyle;
    @Value("${student_img_url_prefix}")
    private String studentImgUrlPrefix;
    @Resource
    private CommonService commonService;
    @Resource
    private BanjiServiceImpl banjiService;
    @Resource
    private SyncStuService syncStuService;
    private final Logger logger = LoggerFactory.getLogger(StudentController.class);

    @ApiOperation("增加学生信息，同时保存学生头像")
    @PostMapping("/add")
    public Result add(String student, String photoData) {
        Student studentObj = JSON.parseObject(student, Student.class);
        File dir = new File(studentImgDir);
        if(!dir.exists()){
            dir.mkdir();
        }
        String message = null;
        String fileNameWithPath;
        if(!TextUtils.isEmpty(photoData)) {
            try {
                String base64RowData = photoData.substring(photoData.indexOf(",")+ 1);
                if(syncStuService.uploadStuPic(base64RowData,studentObj)) {
                    fileNameWithPath = commonService.saveFile(studentImgDir, base64RowData, studentObj.getStudentNumber() , studentObj.getName());
                    if (fileNameWithPath != null) {
                        if(urlStyle.equals(Constant.URL_PATH_STYLE_RELATIVE)) {
                            /**
                             * HeadImg，不保存绝对路径，只保存文件名，方便windows调试。
                             * 方式：xh123456.jpg
                             */
                            studentObj.setHeadImg(studentObj.getStudentNumber().replaceAll("/", "_") + ".jpg");
                        } else {
                            /**
                             * HeadImg，保存绝对路径，方便APP/web调用
                             * 方式：https://eservice-tech.cn/studentImg/10812.jpg
                             */
                            studentObj.setHeadImg(studentImgUrlPrefix + studentObj.getStudentNumber().replaceAll("/", "_") + ".jpg");
                        }
                    } else {
                        message = "failed to save file, no student added of " + studentObj.getName();
                        throw new RuntimeException();
                    }
                } else {
                    message = "Upload to  face platform failed, student name: " + studentObj.getName();
                    throw new RuntimeException();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return ResultGenerator.genFailResult(e.getMessage() + "," + message);
            }
        } else {
            return ResultGenerator.genFailResult("照片不能为空！");
        }
        studentObj.setCreateTime(new Date());
        studentService.save(studentObj);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/delete")
    @Transactional
    public Result delete(@RequestParam String student) {
        if(student != null) {
            Student studentObj = JSON.parseObject(student, Student.class);
            //TODO:需要删除对应照片以及人脸平台中对应的学生
            studentObj.setValid(0);
            studentService.update(studentObj);
        } else {
            ResultGenerator.genFailResult("参数不能为空！");
        }
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/update")
    @Transactional
    public Result update(String student, String photoData) {
        File dir = new File(studentImgDir);
        if(!dir.exists()){
            dir.mkdir();
        }
        String message = null;
        String fileNameWithPath;
        Student studentObj = JSON.parseObject(student, Student.class);
        if(photoData != null && !"".equals(photoData)) {
            try {
                String base64RowData = photoData.substring(photoData.indexOf(",")+ 1);
                if(syncStuService.uploadStuPic(base64RowData,studentObj)) {
                    fileNameWithPath = commonService.saveFile(studentImgDir, base64RowData, studentObj.getStudentNumber(), studentObj.getName());
                    if (fileNameWithPath == null) {
                        message = "failed to save file, no student added of " + studentObj.getName();
                        throw new RuntimeException();
                    }
                } else {
                    message = "Upload to  face platform failed, student name: " + studentObj.getName();
                    throw new RuntimeException();
                }

            } catch (IOException e) {
                e.printStackTrace();
                return ResultGenerator.genFailResult(e.getMessage() + "," + message);
            }
        }
        studentObj.setUpdateTime(new Date());
        studentService.update(studentObj);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/detail")
    public Result detail(@RequestParam Integer id) {
        Student student = studentService.findById(id);
        return ResultGenerator.genSuccessResult(student);
    }

    @PostMapping("/transferPhoto")
    public Result transferPhoto(@RequestParam String srcDirName, @RequestParam String desDirName) {
        File srcDir = new File(studentImgDir + srcDirName);
        if(srcDir.exists()) {
            File desDir = new File(studentImgDir + desDirName);
            if(!desDir.exists()) {
                desDir.mkdirs();
            } else {
                File[] photoFiles = srcDir.listFiles();
                for (int i = 0; i < photoFiles.length; i++) {
                    try {
                        commonService.reduceImg(photoFiles[i].getAbsolutePath(),desDir + "/" + photoFiles[i].getName(), 1000, 1200, false);
                    } catch (IOException e) {
                        System.out.println("转换错误：" + photoFiles[i].getName());
                        e.printStackTrace();
                    }
                }
            }
        }
        return ResultGenerator.genSuccessResult("转换完成");
    }

    @PostMapping("/list")
    public Result list(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<Student> list = studentService.findAll();
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @PostMapping("/totalFaceNumber")
    public Result totalFaceNumber() {
        return ResultGenerator.genSuccessResult(syncStuService.getStudentList().size());
    }

    @PostMapping("/totalPlatformNumber")
    public Result totalPlatformNumber() {
        return ResultGenerator.genSuccessResult(studentService.findAll().size());
    }

    @PostMapping("/syncStuPicToFacePlatform")
    public Result syncStuPicToFacePlatform() {
        List<Student> platformStuList = studentService.findAll();
        return ResultGenerator.genSuccessResult(syncStuService.syncStuPicToFacePlatform(platformStuList));
    }


    @ApiOperation("根据校车编号和模式（班次），返回该校车班次的计划乘坐的学生")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "busNumber", value = "校车编号，比如 xc001",required = true),
            @ApiImplicitParam(paramType = "query",name = "busMode", value = "校车班次，内容限于“上学”、“放学”两种，晚班没有固定乘坐计划所以不支持",required = true)
    })
    @PostMapping("/getPlannedStudentsByBusNumberAndBusMode")
    public Result getPlannedStudentsByBusNumberAndBusMode(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size,
                                                    @RequestParam() String busNumber,
                                                    @RequestParam() String busMode) {
        PageHelper.startPage(page, size);
        List<StudentInfo> list = studentService.getPlannedStudentsByBusNumberAndBusMode(busNumber, busMode);
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @ApiOperation("根据学号查找某学生详细信息")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query",name = "studentNumber", value = "学生的学号") })
    @PostMapping("/getStudentInfo")
    public Result getStudentInfo(@RequestParam String studentNumber) {
        Student student = studentService.getStudentInfo(studentNumber);
        return ResultGenerator.genSuccessResult(student);
    }

    @ApiOperation("根据班级名称获取学生列表")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query",name = "gradeName", value = "年级名称,比如 1年级"),
            @ApiImplicitParam(paramType = "query",name = "className", value = "班级名称，比如 1(2)"),
            @ApiImplicitParam(paramType = "query",name = "queryKey", value = "关键字，包括学号，包括学生姓名，比如张三" +
                    "包括站点名称，比如 中山南一路500弄，校车编号 比如 1 ，任何一个匹配都会返回")})
    @PostMapping("/getStudents")
    public Result getStudents(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size,
                              String gradeName,
                              String className,
                              String queryKey) {

        PageHelper.startPage(page, size);
        List<StudentInfo> students = studentService.getStudents(gradeName,className,queryKey);
        PageInfo pageInfo = new PageInfo(students);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @ApiOperation("根据校车编号+模式（上学放学）+站点 等等 查找计划乘坐的学生列表,不传参数则不限制对应的条件")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "busNumber", value = "校车编号" ),
            @ApiImplicitParam(paramType = "query",name = "busMode", value = "校车班次，内容限于“上学”、“放学”两种，上学放学每个学生都是同辆车，这个参数传不传都一样"),
            @ApiImplicitParam(paramType = "query",name = "busStation", value = "校车站点名称，比如1号路口" ),
            @ApiImplicitParam(paramType = "query",name = "gradeName", value = "年级，比如 1年级，(zj) 1年级"),
            @ApiImplicitParam(paramType = "query",name = "className", value = "班级，比如 1(1) ，注意括号小写")})
    @PostMapping("/getPlannedStudents")
    public Result getPlannedStudents(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size,
                                     String busNumber,
                                     String busMode,
                                     String busStation,
                                     String gradeName,
                                     String className) {
        PageHelper.startPage(page, size);
        List<StudentInfo> list = studentService.getPlannedStudents(busNumber, busMode,busStation,gradeName,className);
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }
    
    @ApiOperation("从xls excel里读取学生信息(不包括线路和上下车站点)")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query",name = "fileName", value = "excel带路径文件名，比如C:\\Users\\wxm\\Desktop\\shzx_doc\\国际部学生基本信息20190126_修正.xls") })
    @PostMapping("/parseInfoFromExcel")
    public Result parseInfoFromExcel(@RequestParam String fileName) {
        Result banji = studentService.readFromExcel(fileName);
        return ResultGenerator.genSuccessResult(banji);
    }

    @ApiOperation("从xls excel里读取学生 线路和上下车站点 信息")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query",name = "fileName", value = "excel带路径文件名，比如C:\\Users\\wxm\\Desktop\\shzx_doc\\校车线路上传模版_需求_2019_0127(1)-0220.xls") })
    @PostMapping("/parseInfoFromExcelForBoardStation")
    public Result parseInfoFromExcelForBoardStation(@RequestParam String fileName) {
        Result banji = studentService.parseInfoFromExcelForBoardStation(fileName);
        return ResultGenerator.genSuccessResult(banji);
    }

    @ApiOperation("读取学生的头像文件（放在特定目录下student_img_dir）的命名来填充学生的头像字段，比如某学生的头像文件为 14111.jpg 则在该学生的head_img字段填入14111.jpg;返回列表显示照片存在，但是数据库中不存在的文件名。")
    @PostMapping("/getAndInsertStudentHeadImg")
    public Result getAndInsertStudentHeadImg() {
        List<String> notDBExistList = studentService.getAndInsertStudentHeadImg();
        return ResultGenerator.genSuccessResult(notDBExistList);
    }

    @ApiOperation("把特定目录下student_img_dir_number的照片文件（学号.xxx）都改名为： 学号_姓名.xxx，返回重名等意外的情况")
    @PostMapping("/renameNumberStudentPicFile")
    public Result renameNumberStudentPicFile() {
        List<String> notDBExistList = studentService.renameNumberStudentPicFile();
        return ResultGenerator.genSuccessResult(notDBExistList);
    }

    //这个接口待测
    @ApiOperation("把特定目录下student_img_dir_name的照片文件（姓名.xxx）都改名为： 学号_姓名.xxx，返回重名等意外的情况")
    @PostMapping("/renameNameStudentPicFile")
    public Result renameNameStudentPicFile() {
        List<String> notDBExistList = studentService.renameNameStudentPicFile();
        return ResultGenerator.genSuccessResult(notDBExistList);
    }

    @ApiOperation("参数传入上中的班车URL， 根据URL返回的数据，创建学生（URL只包括姓名，班级，学号）。返回新增的学生数量 ")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query",name = "urlStr", value = " url地址 ")})
    @PostMapping("/getURLContentAndCreateStu")
    public Result getURLContentAndCreateStu(@RequestParam(defaultValue = "http://app.shs.cn/ydpt/ws/buse/students?sign=865541ccd3e52ba8ad0d16052cc25903&sendTime=1551664022761")
                                                        String urlStr) {

        Integer addedStuSum = 0;
        String strFromUrl = CommonService.getUrlResponse(urlStr);
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
                student.setValid(1);

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
            logger.warn(" exception: " + e.toString());
            return ResultGenerator.genFailResult(" exception: " + e.toString());
        }
        return ResultGenerator.genSuccessResult("addedStuSum " + addedStuSum + " is added");
    }
}
