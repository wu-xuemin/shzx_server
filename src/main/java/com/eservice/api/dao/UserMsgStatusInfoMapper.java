package com.eservice.api.dao;

import com.eservice.api.core.Mapper;
import com.eservice.api.model.messages.MessagesInfo;
import com.eservice.api.model.user_msg_status_info.UserMsgStatusInfo;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserMsgStatusInfoMapper extends Mapper<UserMsgStatusInfo> {
    List<MessagesInfo> getMessageInfo(@Param("userAccount") String userAccount);
    @Select("SELECT * FROM user_msg_status_info where user_msg_status_info.message_id = #{messageId} and user_msg_status_info.`user` = #{userID}")
    UserMsgStatusInfo getTheUserMsgStatusInfo(@Param("userID") Integer userID, @Param("messageId") Integer messageId);
    @Select("SELECT user_msg_status_info.*  from user_msg_status_info  LEFT JOIN user on user_msg_status_info.`user` = `user`.id " +
            "where user.account = #{userAccount}")
    List<UserMsgStatusInfo> getTheUserAllMsgStatusInfo(@Param("userAccount") String userAccount);
}