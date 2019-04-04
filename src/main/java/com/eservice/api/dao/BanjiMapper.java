package com.eservice.api.dao;

import com.eservice.api.core.Mapper;
import com.eservice.api.model.banji.Banji;
import com.eservice.api.model.user.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface BanjiMapper extends Mapper<Banji> {

    @Select("SELECT * from banji LEFT JOIN user on banji.charge_teacher = `user`.id ")
    List<User> getChargeTeachers();

    @Select("SELECT * from banji LEFT JOIN user on banji.charge_teacher = `user`.id where banji.class_name = #{banjiName} and banji.grade = #{gradeName}")
    User getTheChargeTeacher(@Param("gradeName") String gradeName, @Param("banjiName") String banjiName);
}