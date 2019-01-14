package com.eservice.api.service.impl;

import com.eservice.api.dao.UserMsgStatusInfoMapper;
import com.eservice.api.model.messages.MessagesInfo;
import com.eservice.api.model.user_msg_status_info.UserMsgStatusInfo;
import com.eservice.api.service.UserMsgStatusInfoService;
import com.eservice.api.core.AbstractService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;


/**
* Class Description: xxx
* @author Wilson Hu
* @date 2019/01/11.
*/
@Service
@Transactional
public class UserMsgStatusInfoServiceImpl extends AbstractService<UserMsgStatusInfo> implements UserMsgStatusInfoService {
    @Resource
    private UserMsgStatusInfoMapper userMsgStatusInfoMapper;

    public List<MessagesInfo> getMessageInfo(String userAccount){
        return userMsgStatusInfoMapper.getMessageInfo(userAccount);
    }
}
