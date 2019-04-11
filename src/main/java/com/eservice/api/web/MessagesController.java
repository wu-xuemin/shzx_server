package com.eservice.api.web;
import com.eservice.api.core.Result;
import com.eservice.api.core.ResultGenerator;
import com.eservice.api.model.messages.Messages;
import com.eservice.api.model.user.User;
import com.eservice.api.model.user_msg_status_info.UserMsgStatusInfo;
import com.eservice.api.service.MessagesService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
* Class Description: xxx
* @author Wilson Hu
* @date 2019/01/05.
*/
@RestController
@RequestMapping("/messages")
@Api(description = "推送消息的管理")
public class MessagesController {
    @Resource
    private MessagesServiceImpl messagesService;
    @Resource
    private UserMsgStatusInfoServiceImpl userMsgStatusInfoService;
    @Resource
    private UserServiceImpl userService;

    @PostMapping("/add")
    public Result add(Messages messages) {
        messagesService.saveAndGetID(messages);

        /**
         * 增加消息时，也新增已读未读（每个busMom都增加一条未读）
         */
        List<User> allBusMom = userService.findAllBusMom();
        for (int i = 0; i <allBusMom.size(); i++) {
            UserMsgStatusInfo userMsgStatusInfo = new UserMsgStatusInfo();
            userMsgStatusInfo.setMessageId(messages.getId());
            userMsgStatusInfo.setStatus(Constant.MSG_STATUS_UNREAD);
            userMsgStatusInfo.setUser(allBusMom.get(i).getId());
            userMsgStatusInfoService.save(userMsgStatusInfo);
        }
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/delete")
    public Result delete(@RequestParam Integer id) {
        messagesService.deleteById(id);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/update")
    public Result update(Messages messages) {
        messagesService.update(messages);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/detail")
    public Result detail(@RequestParam Integer id) {
        Messages messages = messagesService.findById(id);
        return ResultGenerator.genSuccessResult(messages);
    }

    @PostMapping("/list")
    public Result list(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<Messages> list = messagesService.findAll();
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @ApiOperation("根据条件查询信息，目前参数 标题")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query",name = "title", value = "标题关键字")})
    @PostMapping("/getMessages")
    public Result getMessages(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size,
                              @RequestParam String title) {
        PageHelper.startPage(page, size);
        List<Messages> list = messagesService.getMessages(title);
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

}
