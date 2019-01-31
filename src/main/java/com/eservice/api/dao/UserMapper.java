package com.eservice.api.dao;

import com.eservice.api.core.Mapper;
import com.eservice.api.model.user.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserMapper extends Mapper<User> {

    User selectByAccount(@Param("account") String account);

    User requestLogin(@Param("account") String account, @Param("password") String password );

    List<User> selectUsers(@Param("account")String account, @Param("name")String name, @Param("roleId")Integer roleId, @Param("valid")Integer valid);

    @Select("select * from user where role_id = 3")
    List<User> findAllBusMom();
}