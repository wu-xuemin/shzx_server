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

    private final Logger logger = LoggerFactory.getLogger(MessagesController.class);
    @PostMapping("/add")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query",name = "sendTime", value = "发布时间, 比如 Tue Apr 11 2019 11:12:12 GMT+0800 ", required = true),
            @ApiImplicitParam(paramType = "query",name = "title", value = "标题", required = true),
            @ApiImplicitParam(paramType = "query",name = "publisher", value = "发布者", required = true),
            @ApiImplicitParam(paramType = "query",name = "readCount", value = "已读次数，这里是新增，后台会强设为0"),
            @ApiImplicitParam(paramType = "query",name = "content", value = "消息内容", required = true)})
    public Result add(Messages messages) {
        messages.setReadCount(0);
        logger.info("add Message: " + messages.getTitle() + ", " + messages.getContent() + ", by " + messages.getPublisher() );
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
                              String title) {
        PageHelper.startPage(page, size);
        List<Messages> list = messagesService.getMessages(title);
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

}
