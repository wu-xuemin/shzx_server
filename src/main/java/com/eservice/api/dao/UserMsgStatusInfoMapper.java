package com.eservice.api.dao;

import com.eservice.api.core.Mapper;
import com.eservice.api.model.messages.MessagesInfo;
import com.eservice.api.model.user_msg_status_info.UserMsgStatusInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMsgStatusInfoMapper extends Mapper<UserMsgStatusInfo> {
    List<MessagesInfo> getMessageInfo(@Param("userAccount") String userAccount);
}