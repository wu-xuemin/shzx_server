package com.eservice.api.web;
import com.eservice.api.core.Result;
import com.eservice.api.core.ResultGenerator;
import com.eservice.api.model.messages.MessagesInfo;
import com.eservice.api.model.user_msg_status_info.UserMsgStatusInfo;
import com.eservice.api.service.UserMsgStatusInfoService;
import com.eservice.api.service.impl.UserMsgStatusInfoServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
* Class Description: xxx
* @author Wilson Hu
* @date 2019/01/11.
*/
@RestController
@RequestMapping("/user/msg/status/info")
@Api(description = "用户的消息管理（已读未读）")
public class UserMsgStatusInfoController {
    @Resource
    private UserMsgStatusInfoServiceImpl userMsgStatusInfoService;

    @PostMapping("/add")
    public Result add(UserMsgStatusInfo userMsgStatusInfo) {
        userMsgStatusInfoService.save(userMsgStatusInfo);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/delete")
    public Result delete(@RequestParam Integer id) {
        userMsgStatusInfoService.deleteById(id);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/update")
    public Result update(UserMsgStatusInfo userMsgStatusInfo) {
        userMsgStatusInfoService.update(userMsgStatusInfo);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/detail")
    public Result detail(@RequestParam Integer id) {
        UserMsgStatusInfo userMsgStatusInfo = userMsgStatusInfoService.findById(id);
        return ResultGenerator.genSuccessResult(userMsgStatusInfo);
    }

    @PostMapping("/list")
    public Result list(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<UserMsgStatusInfo> list = userMsgStatusInfoService.findAll();
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }
}
