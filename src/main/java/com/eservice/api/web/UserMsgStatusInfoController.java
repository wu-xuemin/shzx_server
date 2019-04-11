package com.eservice.api.web;
import com.eservice.api.core.Result;
import com.eservice.api.core.ResultGenerator;
import com.eservice.api.model.messages.Messages;
import com.eservice.api.model.messages.MessagesInfo;
import com.eservice.api.model.user.User;
import com.eservice.api.model.user_msg_status_info.UserMsgStatusInfo;
import com.eservice.api.service.UserMsgStatusInfoService;
import com.eservice.api.service.common.Constant;
import com.eservice.api.service.impl.MessagesServiceImpl;
import com.eservice.api.service.impl.UserMsgStatusInfoServiceImpl;
import com.eservice.api.service.impl.UserServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    @Resource
    private UserServiceImpl userService;
    @Resource
    private MessagesServiceImpl messagesService;

    private final Logger logger = LoggerFactory.getLogger(UserMsgStatusInfoController.class);
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

    @ApiOperation("更新某用户某个消息的已读未读状态")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query",name = "userAccount", value = "用户的账号",required = true),
            @ApiImplicitParam(paramType = "query",name = "messageId", value = "消息的ID",required = true),
            @ApiImplicitParam(paramType = "query",name = "msgStatus", value = "消息的状态，只能是“已读”或者“未读” ",required = true)})
    @PostMapping("/updateUserMsgStatus")
    public Result updateUserMsgStatus(String userAccount, Integer msgId,String msgStatus) {
        User user = userService.selectByAccount(userAccount);
        if(user == null) {
            return ResultGenerator.genFailResult("Can not find the user by account: " + userAccount);
        }
        Messages message = messagesService.findById(msgId);
        if(message == null){
            return ResultGenerator.genFailResult("Can not find the msg by msgId: " + msgId);
        }
        if(! (msgStatus.equals(Constant.MSG_STATUS_UNREAD) || msgStatus.equals(Constant.MSG_STATUS_IS_READ))){
            return ResultGenerator.genFailResult(" 消息的状态，只能是“已读”或者“未读”");
        }
        UserMsgStatusInfo userMsgStatusInfo = new UserMsgStatusInfo();
        userMsgStatusInfo.setUser(user.getId());
        userMsgStatusInfo.setMessageId(msgId);
        userMsgStatusInfo.setStatus(msgStatus);
        logger.info("updateUserMsgStatus: " + user.getAccount() + ", msgId:" + msgId + ", msgStatus " + msgStatus);
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

    @ApiOperation("根据账号获取该用户的message信息，包括已读未读状态")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query",name = "userAccount", value = "用户账号") })
    @PostMapping("/getMessageInfo")
    public Result getMessageInfo(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size,
                                 @RequestParam String userAccount) {
        PageHelper.startPage(page, size);
        List<MessagesInfo> list = userMsgStatusInfoService.getMessageInfo(userAccount);
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }
}
