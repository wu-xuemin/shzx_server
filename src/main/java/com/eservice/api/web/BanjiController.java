package com.eservice.api.web;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eservice.api.core.Result;
import com.eservice.api.core.ResultGenerator;
import com.eservice.api.model.banji.Banji;
import com.eservice.api.model.user.User;
import com.eservice.api.service.common.CommonService;
import com.eservice.api.service.common.SMSUtils;
import com.eservice.api.service.impl.BanjiServiceImpl;
import com.eservice.api.service.impl.UserServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

/**
* Class Description: xxx
* @author Wilson Hu
* @date 2018/12/17.
*/
@RestController
@RequestMapping("/banji")
@Api(description = "班级信息管理")
public class BanjiController {
    @Resource
    private BanjiServiceImpl banjiService;
    @Resource
    private UserServiceImpl userService;

    @Resource
    private SMSUtils smsUtils;

    private final static Logger logger = LoggerFactory.getLogger(BanjiController.class);
    @PostMapping("/add")
    public Result add(Banji banji) {
        banji.setCreateTime(new Date());
        banjiService.save(banji);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/delete")
    public Result delete(@RequestParam Integer id) {
        banjiService.deleteById(id);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/update")
    public Result update(Banji banji) {
        banji.setUpdateTime(new Date());
        banjiService.update(banji);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/detail")
    public Result detail(@RequestParam Integer id) {
        Banji banji = banjiService.findById(id);
        return ResultGenerator.genSuccessResult(banji);
    }

    @PostMapping("/list")
    public Result list(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<Banji> list = banjiService.findAll();
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @PostMapping("/list/info")
    public Result listInfo(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size,@RequestParam String chargeTeacher) {
        PageHelper.startPage(page, size);
        Condition condition=new Condition(Banji.class);
        condition.createCriteria().andEqualTo("chargeTeacher",chargeTeacher);
        List<Banji> list = banjiService.findByCondition(condition);
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @ApiOperation("从xls excel里读取班级信息")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query",name = "fileName", value = "excel带路径文件名，比如C:\\Users\\wxm\\Desktop\\shzx_doc\\国际部学生基本信息20190126.xls") })
    @PostMapping("/parseInfoFromExcel")
    public Result parseInfoFromExcel(@RequestParam String fileName) {
        Result banji = banjiService.readFromExcel(fileName);
        return ResultGenerator.genSuccessResult(banji);
    }

    @ApiOperation("根据年级、班级查询当天缺乘，并发送短信")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query",name = "gradeName", value = "年级，比如 1年级，(zj) 1年级"),
            @ApiImplicitParam(paramType = "query",name = "banjiName", value = "班级，比如 1(1)，注意括号小写") })
    @PostMapping("/sendSMStest")
    public Result sendSMStest(@RequestParam String gradeName, @RequestParam String banjiName) {
        String strAbsenceDetail = null;
        strAbsenceDetail = banjiService.getAbsenceTodayByGradeClass(gradeName,banjiName);
        logger.info( strAbsenceDetail);

        smsUtils.send(new String[]{"13588027825"},strAbsenceDetail);
        return ResultGenerator.genSuccessResult(strAbsenceDetail);
    }

    @ApiOperation("返回所有班级的当前的班主任(而非仅仅是班主任就返回)")
    @PostMapping("/getChargeTeachers")
    public Result getChargeTeachers(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<User> list = banjiService.getChargeTeachers();
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @ApiOperation("返回1-8年级的当前的班主任 ")
    @PostMapping("/get1To8GradeChargeTeachers")
    public Result get1To8GradeChargeTeachers(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<User> list = banjiService.get1To8GradeChargeTeachers();
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @ApiOperation("根据班级返回 对应的班主任")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query",name = "gradeName", value = "年级，比如 1年级，(zj) 1年级"),
                        @ApiImplicitParam(paramType = "query",name = "banjiName", value = "班级，比如 1(1)，注意括号小写") })
    @PostMapping("/getTheChargeTeacher")
    public Result getTheChargeTeacher(String gradeName, String banjiName) {
        User bzr = banjiService.getTheChargeTeacher(gradeName,banjiName);
        return ResultGenerator.genSuccessResult(bzr);
    }


    @ApiOperation("根据年级返回年级的所有班级 ")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query",name = "gradeName", value = "年级，比如 1年级，(zj) 1年级")})
    @PostMapping("/getBanjiListByGrade")
    public Result getBanjiListByGrade(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size,
                                      String gradeName) {
        PageHelper.startPage(page, size);
        List<Banji> list = banjiService.getBanjiListByGrade(gradeName);
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @ApiOperation("查看指定了年级和班级名称的班级是否存在")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query",name = "gradeName", value = "年级，比如 1年级，(zj) 1年级",required = true),
            @ApiImplicitParam(paramType = "query",name = "banjiName", value = "班级，比如 1(1)，注意括号小写",required = true)})
    @PostMapping("/isBanjiExist")
    public Result isBanjiExist(@RequestParam String gradeName,@RequestParam String banjiName) {
        boolean isExist = banjiService.isBanjiExist(gradeName, banjiName);
        return ResultGenerator.genSuccessResult(isExist);
    }

    @ApiOperation("参数传入上中班级URL， 根据URL返回的数据，创建班级（包括年级名称，班级名称，班主任，创建时间）（注意班主任要先创建）。返回新增的班级数量 ")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query",name = "urlStr", value = " url地址 ")})
    @PostMapping("/getURLContentAndCreateBanji")
    public Result getURLContentAndCreateBanji(@RequestParam(defaultValue = "http://app.shs.cn/ydpt/ws/buse/classes?sign=865541ccd3e52ba8ad0d16052cc25903&sendTime=1551664022761")
                                                          String urlStr) {

        Integer addedBanjiSum = 0;
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
                banji.setGrade(grade);
                banji.setClassName(banjiName);
                banji.setCreateTime(new Date());

                Class cl = Class.forName("com.eservice.api.model.user.User");
                Field fieldUserAccount = cl.getDeclaredField("account");
                User userExist = null;
                userExist = userService.findBy(fieldUserAccount.getName(), teacher_name);
                if (userExist == null) {
                    return ResultGenerator.genFailResult("Can not find the user by account " + teacher_name);
                } else {
                    banji.setChargeTeacher(userExist.getId());
                }

                /**
                 * 班级不存在时，增加班级
                 */
                if (banjiService.isBanjiExist(grade, banjiName)) {
                    logger.info(" already exist banji: " + banji.getGrade() + "," + banji.getClassName());
                } else {
                    banjiService.save(banji);
                    logger.info("Add banji: " + banji.getGrade() + "," + banji.getClassName());
                    addedBanjiSum++;
                }
            }

        } catch (Exception e) {
            logger.warn(" exception: " + e.toString());
        }
        return ResultGenerator.genSuccessResult("addedBanjiSum " + addedBanjiSum + " is added");
    }
}
