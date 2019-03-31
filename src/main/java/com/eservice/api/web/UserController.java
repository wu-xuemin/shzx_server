package com.eservice.api.web;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eservice.api.core.Result;
import com.eservice.api.core.ResultGenerator;
import com.eservice.api.model.user.User;
import com.eservice.api.model.user.UserDetail;
import com.eservice.api.service.UserService;
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
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
* Class Description: xxx
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

    /**
     * 该值为default值， Android端传入的参数不能为“0”
     */
    private static String ZERO_STRING = "0";

    @ApiOperation("新增用户")
    @PostMapping("/add")
    public Result addStaff(@RequestBody @NotNull User user) {
        if(userService.selectByAccount(user.getAccount()) != null) {
            return ResultGenerator.genFailResult("用户名已存在！");
        }
      //  user.setPassword("password");
        user.setValid(1);
        user.setCreateTime(new Date());
        userService.save(user);
        return ResultGenerator.genSuccessResult();
    }
    @PostMapping("/delete")
    public Result delete(@RequestParam String user) {
        if(user != null) {
            User userObj = JSON.parseObject(user, User.class);
            userObj.setValid(0);
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
    public Result updatePassword(@RequestParam String account, @RequestParam String oldPassword,@RequestParam String newPassword) {

        User user  = userService.requestLogin(account, oldPassword);
        if(user == null) {
            return ResultGenerator.genFailResult("账号/密码 不正确！");
        }else {
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

    @PostMapping("/selectUsers")
    public Result selectUsers(@RequestParam(defaultValue = "0") Integer page,
                              @RequestParam(defaultValue = "0") Integer size,
                              String account,
                              String name,
                              Integer roleId,
                              Integer valid) {
        PageHelper.startPage(page, size);
        List<User> list = userService.selectUsers(account,name,roleId,valid);
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @ApiOperation("带meid登陆，如果参数meid号为空则不验证meid")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query",name = "meid", value = "设备的meid号") })
    @PostMapping("/requestLogin")
    public Result requestLogin(@RequestParam String account, @RequestParam String password, @RequestParam(defaultValue = "0") String meid) {
        boolean result = true;

        if(account == null || "".equals(account)) {
            return ResultGenerator.genFailResult("账号不能为空！");
        } else if(password == null || "".equals(password)) {
            return ResultGenerator.genFailResult("密码不能为空！");
        }else {
            //移动端MEID值需要传入，且不为“0”
            if(!ZERO_STRING.equals(meid)) {
                if(deviceService.findDeviceByMEID(meid) == null) {
                    return ResultGenerator.genFailResult("设备没有登陆权限！");
                }
            }
            User user = userService.requestLogin(account, password);
            if(user == null) {
                return ResultGenerator.genFailResult("账号或密码不正确！");
            }else {
                ///mqttMessageHelper.sendToClient("topic/client/2", JSON.toJSONString(userDetail));
                return ResultGenerator.genSuccessResult(user);
            }
        }
    }

    @PostMapping("/syncBusMomPicToFacePlatform")
    public Result syncBusMomPicToFacePlatform() {
        List<User> platformBusMomList = userService.findAllBusMom();
        return ResultGenerator.genSuccessResult(syncBusMomService.syncBusMomPicToFacePlatform(platformBusMomList));
    }

    @PostMapping("totalBusMomNumber")
    public Result totalBusMomNumber() {
        List<User> platformBusMomList = userService.findAllBusMom();
        return ResultGenerator.genSuccessResult(platformBusMomList.size());
    }

    @PostMapping("totalBusMomFaceNumber")
    public Result totalBusMomFaceNumber() {
        List<WinVisitorRecord> platformBusMomList = syncBusMomService.getBusMonList();
        return ResultGenerator.genSuccessResult(platformBusMomList.size());
    }

    @ApiOperation("从xls excel里读取班主任信息")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query",name = "fileName", value = "excel带路径文件名，比如C:\\Users\\wxm\\Desktop\\shzx_doc\\国际部学生基本信息20190126.xls") })
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
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query",name = "fileName", value = "excel带路径文件名，比如C:\\Users\\wxm\\Desktop\\shzx_doc\\老师信息20190329.xls") })
    @PostMapping("/parseChargeTeacherPhoneFromExcel")
    public Result parseChargeTeacherPhoneFromExcel(@RequestParam String fileName) {
        Result banji = userService.parseChargeTeacherPhoneFromExcel(fileName);
        return ResultGenerator.genSuccessResult(banji);
    }

    @ApiOperation("从xls excel里读取巴士妈妈和司机信息")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query",name = "fileName",
            value = "excel带路径文件名，比如C:\\Users\\wxm\\Desktop\\shzx_doc\\校车线路上传模版_需求_2019_0201-新格式.xls") })
    @PostMapping("/parseBusMomDriverFromExcel")
    public Result parseBusMomDriverFromExcel(@RequestParam String fileName) {
        Result banji = userService.parseBusMomDriverFromExcel(fileName);
        return ResultGenerator.genSuccessResult(banji);
    }

    @ApiOperation("读取user的头像文件（放在特定目录下user_img_dir）的命名来填充头像字段，比如某user头像文件为 busmom11_谢正明.jpg 则在该user的head_img字段填入busmom11_谢正明.jpg;返回列表显示照片存在，但是数据库中不存在的文件名。")
    @PostMapping("/getAndInsertUserHeadImg")
    public Result getAndInsertUserHeadImg() {
        List<String> notDBExistList = userService.getAndInsertUserHeadImg();
        return ResultGenerator.genSuccessResult(notDBExistList);
    }
}
