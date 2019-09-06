package com.eservice.api.web;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eservice.api.core.Result;
import com.eservice.api.core.ResultGenerator;
import com.eservice.api.model.banji.Banji;
import com.eservice.api.model.banji.BanjiInfo;
import com.eservice.api.model.user.User;
import com.eservice.api.service.common.CommonService;
import com.eservice.api.service.common.Constant;
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
import java.util.ArrayList;
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
        List<Banji> list = banjiService.listByClassName();
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

    @ApiOperation("根据年级、班级查询当天缺乘(分上学、放学)，并发送短信")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query",name = "gradeName", value = "年级，比如 1年级，(zj) 1年级",required = true),
            @ApiImplicitParam(paramType = "query",name = "banjiName", value = "班级，比如 1(1)，注意括号小写",required = true),
            @ApiImplicitParam(paramType = "query",name = "busMode", value = "查询的班次， 只能“上学”、“放学”，不能查晚班没有缺乘概念 ",required = true)})
    @PostMapping("/sendSMStest")
    public Result sendSMStest(@RequestParam String gradeName,
                              @RequestParam String banjiName,
                              @RequestParam String busMode) {
        String strAbsenceDetail = null;
        strAbsenceDetail = banjiService.getAbsenceTodayByGradeClass(gradeName,banjiName,busMode);
        logger.info( strAbsenceDetail);

//        smsUtils.send(new String[]{"15715766877","13588027825"},strAbsenceDetail);


        List<Banji> banji1to8List = banjiService.getBanji1to8List();
        //9-12年级除外的班级，即1-8年级才需要发送缺乘短信
        List<User> bzr1To8GradeList = banjiService.get1To8GradeChargeTeachers();

        for (int k= 0; k< banji1to8List.size(); k++) {
            logger.info(banji1to8List.get(k).getClassName());
        }
        for (int j=0;j < bzr1To8GradeList.size(); j++){
            logger.info(bzr1To8GradeList.get(j).getName() + "/" + bzr1To8GradeList.get(j).getPhone());
        }

        //  班主任电话，size为1的数组
        ArrayList< String[] > bzr1To8GradePhoneList = new ArrayList<>();
        for (int i = 0; i <bzr1To8GradeList.size() ; i++) {
            if(bzr1To8GradeList.get(i).getPhone() != null) { // && !bzr1To8GradeList.get(i).getPhone().isEmpty()
                //“Fake” 只是为了转为数组形式
                //// 这里todo
                bzr1To8GradePhoneList.add(bzr1To8GradeList.get(i).getPhone().split("Fake"));
            } else {
                logger.info(bzr1To8GradeList.get(i).getName() + ", Phone is empty");
            }
        }

        // 正式用。 1-8年级才需要发送缺乘短信
        for (int i = 0; i <bzr1To8GradePhoneList.size() ; i++) {
            strAbsenceDetail = banjiService.getAbsenceTodayByGradeClass(banji1to8List.get(i).getGrade(),banji1to8List.get(i).getClassName(),Constant.BUS_MODE_AFTERNOON);
            logger.info(i + "下午 Try send 短信内容：" + strAbsenceDetail);
//            smsUtils.send(bzr1To8GradePhoneList.get(i),strAbsenceDetail);

            logger.info( "Sent SMS to " + bzr1To8GradePhoneList.get(i)[0] + ":" + strAbsenceDetail);
        }
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

    @ApiOperation("返回1-8年级的班级 ")
    @PostMapping("/getBanji1to8List")
    public Result getBanji1to8List(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<Banji> list = banjiService.getBanji1to8List();
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @ApiOperation("根据班级返回 该班级的详细信息（包括班主任）")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query",name = "gradeName", value = "年级，比如 1年级，(zj) 1年级"),
                        @ApiImplicitParam(paramType = "query",name = "banjiName", value = "班级，比如 1(1)，注意括号小写") })
    @PostMapping("/getTheChargeTeacher")
    public Result getTheChargeTeacher(String gradeName, String banjiName) {
        BanjiInfo bzr = banjiService.getTheChargeTeacher(gradeName,banjiName);
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

    /**
     * 注意生成顺序 班主任、busMom、班级、站点、校车、线路、学生 （目前少司机）
     */
    @ApiOperation("参数传入上中班级URL， 根据URL返回的数据，创建班级（包括年级名称，班级名称，班主任，创建时间）（注意班主任要先创建）。返回新增的班级数量 ")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query",name = "urlStr", value = " url地址 ")})
    @PostMapping("/getURLContentAndCreateBanji")
    public Result getURLContentAndCreateBanji(@RequestParam(defaultValue = Constant.SHZX_URL_GET_CLASS)
                                                          String urlStr) {
        Integer addedBanjiSum = 0;
        addedBanjiSum = banjiService.getURLContentAndCreateBanji(urlStr);
        return ResultGenerator.genSuccessResult("addedBanjiSum " + addedBanjiSum + " is added");
    }

    @ApiOperation("根据班主任账号返回班级详情")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query",name = "bzrAccount", value = "班主任账号 比如宋丽萍",required = true)})
    @PostMapping("/getBanjiInfoByBzr")
    public Result getBanjiInfoByBzr(@RequestParam String gradeName,@RequestParam String bzrAccount) {
        BanjiInfo banjiInfo = banjiService.getBanjiInfoByBzr(bzrAccount);
        return ResultGenerator.genSuccessResult(banjiInfo);
    }

    /**
     * 检查班级数据： 检查班级的班主任是否为空。
     * @return
     */
    @PostMapping("/checkBanjiData")
    public Result checkBanjiData() {
        List<Banji> banjiList = banjiService.checkBanjiData();

        return  ResultGenerator.genSuccessResult(banjiList);
    }
}
