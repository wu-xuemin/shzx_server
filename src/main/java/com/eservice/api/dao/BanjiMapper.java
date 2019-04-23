package com.eservice.api.dao;

import com.eservice.api.core.Mapper;
import com.eservice.api.model.banji.Banji;
import com.eservice.api.model.banji.BanjiInfo;
import com.eservice.api.model.user.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface BanjiMapper extends Mapper<Banji> {

    @Select("SELECT * from banji LEFT JOIN user on banji.charge_teacher = `user`.id ")
    List<User> getChargeTeachers();

    @Select("SELECT * from banji LEFT JOIN user on banji.charge_teacher = `user`.id where banji.class_name = #{banjiName} and banji.grade = #{gradeName}")
    BanjiInfo getTheChargeTeacher(@Param("gradeName") String gradeName, @Param("banjiName") String banjiName);

    @Select("SELECT * from banji where banji.grade = #{gradeName}")
    List<Banji> getBanjiListByGrade(@Param("gradeName") String gradeName);

    // <9 刚好可以查出来低于9的年级
    @Select("SELECT * from banji LEFT JOIN user on banji.charge_teacher = `user`.id WHERE banji.grade < 9 ")
    List<User> get1To8GradeChargeTeachers();

    @Select("SELECT * from banji WHERE banji.grade = #{gradeName} and banji.class_name = #{banjiName} ")
    List<Banji> isBanjiExist(@Param("gradeName") String gradeName, @Param("banjiName")String banjiName);

    @Select("SELECT * from banji LEFT JOIN user on banji.charge_teacher = `user`.id where user.account = #{bzrAccount}")
    BanjiInfo getBanjiInfoByBzr(@Param("bzrAccount") String bzrAccount);

    @Select("SELECT * from banji ORDER BY class_name+0")
    List<Banji> listByClassName();
}