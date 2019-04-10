package com.eservice.api.dao;

import com.eservice.api.core.Mapper;
import com.eservice.api.model.messages.Messages;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface MessagesMapper extends Mapper<Messages> {
    @Select("SELECT * from messages WHERE messages.title like CONCAT('%', '${title}', '%')")
    List<Messages> getMessages(@Param("title")String title);
}