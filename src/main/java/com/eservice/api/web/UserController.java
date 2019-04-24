package com.eservice.api.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eservice.api.core.Result;
import com.eservice.api.core.ResultGenerator;
import com.eservice.api.model.user.User;
import com.eservice.api.service.common.CommonService;
import com.eservice.api.service.common.Constant;
import com.eservice.api.service.impl.BanjiServiceImpl;
import com.eservice.api.service.impl.DeviceServiceImpl;
import com.eservice.api.service.impl.UserServiceImpl;
import com.eservice.api.service.park.SyncBusMomService;
import com.eservice.api.service.park.model.WinVisitorRecord;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

/**
 * Class Description: xxx
 *
 * @author Wilson Hu
 * @date 2018/12/17.
 */
@RequestMapping("/user")
@RestController
@Api(description = "用户信息管理")
public class UserController {
    @Resource
    private UserServiceImpl userService;

    @Resource
    private DeviceServiceImpl deviceService;

    @Resource
    private SyncBusMomService syncBusMomService;

    @Resource
    private BanjiServiceImpl banjiService;
    /**
     * 该值为default值， Android端传入的参数不能为“0”
     */
    private static String ZERO_STRING = "0";
    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @ApiOperation("新增用户")
    @PostMapping("/add")
    public Result addStaff(@RequestParam String user) {
        String userStr = user;
        User userObj = JSON.parseObject(user, User.class);
        if (userService.selectByAccount(userObj.getAccount()) != null) {
            return ResultGenerator.genFailResult("用户名已存在！");
        }
        //  user.setPassword("password");
        userObj.setValid(Constant.VALID_YES);
        userObj.setCreateTime(new Date());
        userService.save(userObj);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/delete")
    public Result delete(@RequestParam String user) {
        if (user != null) {
            User userObj = JSON.parseObject(user, User.class);
            userObj.setValid(Constant.VALID_NO);
            userService.update(userObj);
        } else {
            ResultGenerator.genFailResult("参数不能为空！");
        }
        return ResultGenerator.genSuccessResult();
    }

    /**
     * 更新用户密码
     */
    @ApiOperation("更新用户密码")
    @PostMapping("/updatePassword")
    public Result updatePassword(@RequestParam String account, @RequestParam String oldPassword, @RequestParam String newPassword) {

        User user = userService.requestLogin(account, oldPassword);
        if (user == null) {
            return ResultGenerator.genFailResult("账号/密码 不正确！");
        } else {
            user.setPassword(newPassword);
            userService.update(user);
            return ResultGenerator.genSuccessResult("密码更新成功");
        }
    }

    @ApiOperation("更新用户")
    @PostMapping("/update")
    public Result update(String user) {
        User userObj = JSONObject.parseObject(user, User.class);
        userService.update(userObj);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/detail")
    public Result detail(@RequestParam Integer id) {
        User user = userService.findById(id);
        return ResultGenerator.genSuccessResult(user);
    }

    @ApiOperation("获取列表")
    @PostMapping("/list")
    public Result list(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<User> list = userService.findAll();
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", name = "account", value = "账号"),
            @ApiImplicitParam(paramType = "query", name = "name", value = "姓名"),
            @ApiImplicitParam(paramType = "query", name = "roleId", value = "角色，2:管理员,3:busMom,4:班主任,5:司机"),
            @ApiImplicitParam(paramType = "query", name = "valid", value = "是否在职， “1”:在职 “0”:离职")})
    @PostMapping("/selectUsers")
    public Result selectUsers(@RequestParam(defaultValue = "0") Integer page,
                              @RequestParam(defaultValue = "0") Integer size,
                              String account,
                              String name,
                              Integer roleId,
                              Integer valid) {
        PageHelper.startPage(page, size);
        List<User> list = userService.selectUsers(account, name, roleId, valid);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getValid() == 0) {
                list.remove(i);
            }
        }
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @ApiOperation("带meid登陆，如果参数meid号为空则不验证meid")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", name = "meid", value = "设备的meid号")})
    @PostMapping("/requestLogin")
    public Result requestLogin(@RequestParam String account, @RequestParam String password, @RequestParam(defaultValue = "0") String meid) {
        if (account == null || "".equals(account)) {
            return ResultGenerator.genFailResult("账号不能为空！");
        } else if (password == null || "".equals(password)) {
            return ResultGenerator.genFailResult("密码不能为空！");
        } else {
            //移动端MEID值需要传入，且不为“0”
            if (!ZERO_STRING.equals(meid)) {
                if (deviceService.findDeviceByMEID(meid) == null) {
                    return ResultGenerator.genFailResult("设备没有登陆权限！");
                }
            }
            User user = userService.requestLogin(account, password);
            if (user == null) {
                return ResultGenerator.genFailResult("账号或密码不正确！");
            } else {
                return ResultGenerator.genSuccessResult(user);

            }
        }
    }

    @ApiOperation("上中系统单点登录如果是老师，返回老师以及其班级信息，非老师则返回用户信息")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", name = "shzxStaffCode", value = "从上中那边登录后传来的身份ID")})
    @PostMapping("/ShzxCASLogin")
    public Result ShzxCASLogin(@RequestParam String shzxStaffCode) {
        Class cl = null;
        try {
            cl = Class.forName("com.eservice.api.model.user.User");
            Field field = cl.getDeclaredField("schoolStaffCode");
            User user = userService.findBy(field.getName(),shzxStaffCode);
            if(user != null) {
                if (user.getRoleId() == Constant.USER_ROLE_TEACHER) {
                    /**
                     * 如果是老师，带上班级信息
                     */
                    return ResultGenerator.genSuccessResult(banjiService.getBanjiInfoByBzr(user.getAccount()));
                } else {
                    return ResultGenerator.genSuccessResult(user);
                }
            } else {
                return ResultGenerator.genSuccessResult("No user find by shzxStaffCode " + shzxStaffCode);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return ResultGenerator.genFailResult("exception : " + e.toString());
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return ResultGenerator.genFailResult("exception : " + e.toString());
        }
    }

    @PostMapping("/syncBusMomPicToFacePlatform")
    public Result syncBusMomPicToFacePlatform() {
        List<User> platformBusMomList = userService.findAllBusMom();
        return ResultGenerator.genSuccessResult(syncBusMomService.syncBusMomPicToFacePlatform(platformBusMomList));
    }

    @PostMapping("/syncDriverPicToFacePlatform")
    public Result syncDriverPicToFacePlatform() {
        List<User> platformDriverList = userService.findAllDriver();
        return ResultGenerator.genSuccessResult(syncBusMomService.syncDriverPicToFacePlatform(platformDriverList));
    }

    @PostMapping("/totalBusMomNumber")
    public Result totalBusMomNumber() {
        List<User> platformBusMomList = userService.findAllBusMom();
        return ResultGenerator.genSuccessResult(platformBusMomList.size());
    }

    @PostMapping("/totalBusMomFaceNumber")
    public Result totalBusMomFaceNumber() {
        List<WinVisitorRecord> platformBusMomList = syncBusMomService.getBusMonList();
        return ResultGenerator.genSuccessResult(platformBusMomList.size());
    }

    @PostMapping("/totalDriverNumber")
    public Result totalDriverNumber() {
        List<User> platformDriverList = userService.findAllDriver();
        return ResultGenerator.genSuccessResult(platformDriverList.size());
    }

    @PostMapping("/totalDriverFaceNumber")
    public Result totalDriverFaceNumber() {
        List<WinVisitorRecord> platformBusMomList = syncBusMomService.getDriverList();
        return ResultGenerator.genSuccessResult(platformBusMomList.size());
    }

    @ApiOperation("从xls excel里读取班主任信息")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", name = "fileName", value = "excel带路径文件名，比如C:\\Users\\wxm\\Desktop\\shzx_doc\\国际部学生基本信息20190126.xls")})
    @PostMapping("/parseChargeTeacherFromExcel")
    public Result parseChargeTeacherFromExcel(@RequestParam String fileName) {
        Result banji = userService.parseChargeTeacherFromExcel(fileName);
        return ResultGenerator.genSuccessResult(banji);
    }

    /**
     * 更新从另一个 xls excel里读取班主任信息,因为班主任的手机信息和前面不是同一个文件.
     * 这个excel是根据 http://app.shs.cn/ydpt/ws/buse/classes?sign=865541ccd3e52ba8ad0d16052cc25903&sendTime=1551664022761
     * 的返回结果制作的excel。
     */
    @ApiOperation("更新从另一个 xls excel里读取班主任信息,因为班主任的手机信息和前面不是同一个文件")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", name = "fileName", value = "excel带路径文件名，比如C:\\Users\\wxm\\Desktop\\shzx_doc\\老师信息20190329.xls")})
    @PostMapping("/parseChargeTeacherPhoneFromExcel")
    public Result parseChargeTeacherPhoneFromExcel(@RequestParam String fileName) {
        Result banji = userService.parseChargeTeacherPhoneFromExcel(fileName);
        return ResultGenerator.genSuccessResult(banji);
    }

    @ApiOperation("从xls excel里读取巴士妈妈和司机信息")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", name = "fileName",
            value = "excel带路径文件名，比如C:\\Users\\wxm\\Desktop\\shzx_doc\\校车线路上传模版_需求_2019_0201-新格式.xls")})
    @PostMapping("/parseBusMomDriverFromExcel")
    public Result parseBusMomDriverFromExcel(@RequestParam String fileName) {
        Result banji = userService.parseBusMomDriverFromExcel(fileName);
        return ResultGenerator.genSuccessResult(banji);
    }

    @ApiOperation("读取user的头像文件（放在特定目录下user_img_dir）的命名来填充头像字段，比如某user头像文件为 13344449999_busmom11_谢正明.jpg 则在该user的head_img字段填入13344449999_busmom11_谢正明.jpg;返回列表显示照片存在，但是数据库中不存在的文件名。")
    @PostMapping("/getAndInsertUserHeadImg")
    public Result getAndInsertUserHeadImg() {
        List<String> notDBExistList = userService.getAndInsertUserHeadImg();
        return ResultGenerator.genSuccessResult(notDBExistList);
    }

    @ApiOperation("user的头像文件（放在特定目录下user_img_dir）重新命名为 类似 手机号_姓名.jpg")
    @PostMapping("/renameUserPic")
    public Result renameUserPic() {
        List<String> notDBExistList = userService.renameUserPic();
        return ResultGenerator.genSuccessResult(notDBExistList);
    }

    @ApiOperation("参数传入上中的班级URL， 根据URL返回的数据（不包含教师工号），创建班主任（包括账号，姓名，角色，密码，电话，创建时间，在职，不包括教师的工号）。返回新增的 班主任数量 ")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", name = "urlStr", value = " url地址 ")})
    @PostMapping("/getURLContentAndCreateBZR")
    public Result getURLContentAndCreateBZR(@RequestParam(defaultValue = Constant.SHZX_URL_GET_CLASS)
                                                    String urlStr) {

        Integer addedBzrSum = 0;
        String strFromUrl = CommonService.getUrlResponse(urlStr);
        try {
            JSONObject jsonObject = JSON.parseObject(strFromUrl);
            JSONArray ja = jsonObject.getJSONArray("result");
            for (int i = 0; i < ja.size(); i++) {
                User bzr = new User();
                JSONObject jo = ja.getJSONObject(i);
                String teacherName = jo.getString("teacher_name");
                String teacherPhone = jo.getString("teacher_phone");
                String teacherUnionId = jo.getString("tunionId");

                bzr.setCreateTime(new Date());
                //班主任的账号设为和姓名一样
                bzr.setAccount(teacherName);
                bzr.setName(teacherName);
                bzr.setPhone(teacherPhone);
                bzr.setPassword(Constant.USER_DEFAULT_PASSWORD);
                bzr.setRoleId(Constant.USER_ROLE_TEACHER);
                bzr.setCreateTime(new Date());
                bzr.setValid(Constant.VALID_YES);
                bzr.setSchoolStaffCode(teacherUnionId);

                Class cl = Class.forName("com.eservice.api.model.user.User");
                Field fieldUserAccount = cl.getDeclaredField("account");
                User userExist = null;
                userExist = userService.findBy(fieldUserAccount.getName(), teacherName);
                if (userExist == null) {
                    userService.save(bzr);
                    logger.info("added bzr: " + bzr.getAccount());
                    addedBzrSum++;
                } else {
                    logger.info(" already exist account: " + bzr.getAccount() + ",name " + bzr.getName());
                }
            }

        } catch (Exception e) {
            logger.warn(" exception: " + e.toString());
        }
        return ResultGenerator.genSuccessResult("addedBzrSum " + addedBzrSum + " is added");
    }


    @ApiOperation("参数传入上中的班车URL， 根据URL返回的数据，创建BusMom,司机（包括账号，姓名，角色，密码，电话，创建时间，在职）。返回新增的 busMom数量 ")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", name = "urlStr", value = " url地址 ")})
    @PostMapping("/getURLContentAndCreateBusMomAndDriver")
    public Result getURLContentAndCreateBusMomAndDriver(@RequestParam(defaultValue = Constant.SHZX_URL_GET_BUS)
                                                        String urlStr) {
        Integer addedBusMomSum = 0;
        Integer addedBusDriverSum = 0;
        String strFromUrl = CommonService.getUrlResponse(urlStr);
        try {
            JSONObject jsonObject = JSON.parseObject(strFromUrl);
            JSONArray ja = jsonObject.getJSONArray("result");
            for (int i = 0; i < ja.size(); i++) {
                User busMom = new User();
                User busDriver = new User();
                JSONObject jo = ja.getJSONObject(i);
                String busMomName = jo.getString("bus_mom_name");
                String busMomPhone = jo.getString("bus_mom_phone");
                String busdriverName = jo.getString("driver_name");
                String busdriverPhone = jo.getString("driver_phone");

                busMom.setAccount(busMomName);
                busMom.setName(busMomName);
                busMom.setRoleId(Constant.USER_ROLE_BUSMOM);
                busMom.setPassword(Constant.USER_DEFAULT_PASSWORD);
                busMom.setPhone(busMomPhone);
                busMom.setCreateTime(new Date());
                busMom.setValid(Constant.VALID_YES);

                busDriver.setAccount(busdriverName);
                busDriver.setName(busdriverName);
                busDriver.setRoleId(Constant.USER_ROLE_DRIVER);
                busDriver.setPassword(Constant.USER_DEFAULT_PASSWORD);
                busDriver.setPhone(busdriverPhone);
                busDriver.setCreateTime(new Date());
                busDriver.setValid(Constant.VALID_YES);

                Class cl = Class.forName("com.eservice.api.model.user.User");
                Field fieldUserAccount = cl.getDeclaredField("account");
                User busMomExist = null;
                busMomExist = userService.findBy(fieldUserAccount.getName(), busMomName);
                if (busMomExist == null) {
                    userService.save(busMom);
                    logger.info("added busMom: " + busMom.getAccount());
                    addedBusMomSum++;
                } else {
                    logger.info(" already exist busMom: " + busMom.getAccount());
                }

                User busDriverExist = null;
                busDriverExist = userService.findBy(fieldUserAccount.getName(), busdriverName);
                if (busDriverExist == null) {
                    userService.save(busDriver);
                    logger.info("added driver: " + busdriverName);
                    addedBusDriverSum++;
                } else {
                    logger.info(" already exist driver: " + busdriverName);
                }
            }

        } catch (Exception e) {
            logger.warn(" exception: " + e.toString());
        }
        return ResultGenerator.genSuccessResult("addedBusMomSum: " + addedBusMomSum + "  ,addedBusDriverSum: " + addedBusDriverSum );
    }
}
