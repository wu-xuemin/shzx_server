package com.eservice.api.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eservice.api.core.Result;
import com.eservice.api.core.ResultGenerator;
import com.eservice.api.model.banji.Banji;
import com.eservice.api.model.student.Student;
import com.eservice.api.model.student.StudentInfo;
import com.eservice.api.model.user.User;
import com.eservice.api.service.common.CommonService;
import com.eservice.api.service.common.Constant;
import com.eservice.api.service.impl.*;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
@RequestMapping("/common")
@Api(description = "更新信息等")
public class CommonController {
    @Resource
    private StudentServiceImpl studentService;

    @Resource
    private BanjiServiceImpl banjiService;

    @Resource
    private BusStationsServiceImpl busStationsService;

    @Resource
    private BusLineServiceImpl busLineService;

    @Resource
    private UserServiceImpl userService;

    @Resource
    private BusBaseInfoServiceImpl busBaseInfoService;

    private final Logger logger = LoggerFactory.getLogger(CommonController.class);

    @ApiOperation("从上中URL获取所有数据，会替换旧数据, 警告：运行该接口前，需要手工备份数据再删除数据。")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query",name = "password", value = "防止误操作，设置了密码 ")})
    @PostMapping("/refreshAllDataFromURL")
    public Result refreshAllDataFromURL(@RequestParam String password) {

        if(!password.equals("tycljcc")){
            return ResultGenerator.genFailResult("密码不对");
        }
        logger.warn("从上中URL获取所有数据，会替换旧数据, starting...");

        String msg = "";
        //step0. todo: 备份数据库，按顺序清除数据(乘车记录，学生老师班级校车信息等等)。

        //step1 班级信息 (banji)
        Integer banjiCreated = banjiService.getURLContentAndCreateBanji(Constant.SHZX_URL_GET_CLASS);
        msg = banjiCreated + " banji(s) created; ";

        //step2 站点信息 (station_name/remark/fare_rate)
        Integer busStationCreated = busStationsService.getURLContentAndCreateBusStations(Constant.SHZX_URL_GET_BUS);
        msg = msg + busStationCreated + " busStation(s) created; ";

        //step3 busMom和司机，todo: 印象中 URL返回是缺少司机
        String str1 = userService.getURLContentAndCreateBusMomAndDriver(Constant.SHZX_URL_GET_BUS);
        msg = msg + str1 + "; ";

        //step4 busMom和司机的照片,注意有要求,注意有小部分照片命名不对，比如和URL里的手机号不同。
        userService.getAndInsertUserHeadImg();

        //step5 班主任
        String str2 = userService.getURLContentAndCreateBZR(Constant.SHZX_URL_GET_CLASS);
        msg = msg + str2 + "; ";

        //step6 校车信息
        Integer busBaseInfoCreated = busBaseInfoService.getURLContentAndCreateBusBaseInfo(Constant.SHZX_URL_GET_BUS);
        msg = msg + busBaseInfoCreated + " busBaseInfo(s) created; ";

        Result result1 = busBaseInfoService.updateBusBaseInfoDriverAndBusMom(Constant.SHZX_URL_GET_BUS);
        msg = msg + result1.getMessage() +"; ";
        //step7 线路信息
        String str3 = busLineService.getURLContentAndCreateBusSLine(Constant.SHZX_URL_GET_BUS);
        msg = msg + str3 + "; \r\n";

        //step8 学生基本信息
        String str4 = studentService.getURLContentAndCreateStu(Constant.SHZX_URL_GET_STUDENT,Constant.SHZX_URL_GET_BUS);
        msg = msg + str4 + "; ";

        //step9 学生照片
        studentService.getAndInsertStudentHeadImg();

        //补充更新班级的班主任
        banjiService.getURLContentAndCreateBanji(Constant.SHZX_URL_GET_CLASS);

        /**
         * 简单检查数据
         */
        //step1 检查班级
        List<Banji> banjiList = banjiService.checkBanjiData();
        String banjiMsg = "【1】没有班主任的班级：";
        for(int b=0; b<banjiList.size(); b++){
            banjiMsg = banjiMsg + banjiList.get(b).getClassName() + ",";
        }

        //step2 检查busMom/司机
        List<User> userList = userService.checkBusMomDriverData();
        String userMsg = "【2】 照片有问题的busMom/司机：";
        for(int s=0; s<userList.size(); s++){
            userMsg = userMsg + userList.get(s).getAccount() + ",";
        }

        //step3 检查班主任的职工编号是否存在。
        List<User> bzrList = userService.checkBzr();
        String bzrMsg = "【3】没有tunionId的班主任：";
        for(int z=0; z<bzrList.size(); z++){
            bzrMsg = bzrMsg + bzrList.get(z).getAccount() + ",";
        }

        //step4 检查学生照片。
        List<Student> studentImgList = studentService.checkStudentImg();
        String studentImgMsg = "【4】照片有问题的学生：";
        for(int s=0; s<studentImgList.size(); s++){
            studentImgMsg = studentImgMsg + studentImgList.get(s).getName() + ",";
        }

        //step5 检查学生乘车信息。
        List<Student> studentBusList = studentService.checkStudentBus();
        String studentBusMsg = "【5】乘车信息有问题的学生：";
        for(int s=0; s<studentBusList.size(); s++){
            studentBusMsg = studentBusMsg + studentBusList.get(s).getName() + ",";
        }




        return ResultGenerator.genSuccessResult(msg + banjiMsg + userMsg + bzrMsg + studentImgMsg + studentBusMsg);
    }
}
