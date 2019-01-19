package com.eservice.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Date;

@Configuration //标记配置类
@EnableSwagger2 //开启在线接口文档
public class Swagger2Config {

    @Value("${swagger.enable}")
    private boolean enableSwagger;

    /**
     * 添加摘要信息(Docket)
     */
    @Bean
    public Docket controllerApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                        .title("上海中学校车通行管理系统_后端接口文档_" + new Date())
                        .description("补充了/transport/range/getTransportRangeByBusNumberAndBusMode；" +
                                "修复/transport/record/getTransportRecord日期无效问题" +
                                "/student/getPlannedStudents 根据校车编号+模式（早班午班）+站点 查找计划乘坐的学生列表 \n\r " +
                                "/picked/students/info/add 学生签到时用学号替代StudentId " +
                                "/bus/line/getStudentsByBusNumber改为 getStudents，根据校车编号/早午班 来获得该校车/早午班的所有学生，" +
                                "/bus/base/Info/getBusBaseInfo 根据校车编号等 去查询校车详情包括巴士妈妈名字" +
                                "添加后台的学生乘车记录查询功能 /picked/students/info/selectStudentBus/selectStudentBus" +
                                "一些注意事项没有一一标明，\n\r" +
                                "比如，在add时，id号留空就好，不要填写；\n\r" +
                                "比如，在add时参数未填写，导致\"内部错误\"。\n\r " +
                                "比如，在add时，填写了不存在的外键值作为参数会导致\"内部错误\"。 ")
                        .contact(new Contact("wuxuemin", "https://eservice-tech.cn/", "wuxuemin2000@126.com"))
                        .version("版本号:0.1")
                        .build())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.eservice.api.web"))
                .paths(PathSelectors.any())
                .build()
                .enable(enableSwagger);
    }
}