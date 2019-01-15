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
                        .title("上海中学校车通行管理系统_后端接口文档_2019-01-15")
                        .description("请多查看swagger，比如 实时接送记录可以用/transport/record/selectTransportRecord 按条件查询\n\r " +
                                "/bus/line/getBusLineInfoByBusMomAccount()改为getBusLineInfoByBusMomAccountAndBusMode（） \n\r " +
                                "/transport/record/getUnplannedStudents 获取乘坐计划外的学生（临时乘坐）信息 \n\r" +
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