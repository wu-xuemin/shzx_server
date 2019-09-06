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

    @Select("select * from user where role_id = 5")
    List<User> findAllDriver();

    @Select(" SELECT * from user u where u.head_image is null  and ( u.role_id = 3 or u.role_id =5)")
    List<User> checkBusMomDriverData();

    @Select(" SELECT * from user u where u.school_staff_code is null  and ( u.role_id = 4)")
    List<User>checkBzr();
}