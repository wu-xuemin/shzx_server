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
                        .description("getBusLineInfoByBusMomAccountAndBusMode改为getBusLineInfoByBusMomAccount根据巴士妈妈账号 获得巴士线路等信息,每个巴士妈妈都固定一辆校车，会返回固定一辆校车的早班和午班线路（busNumber相同），类似的还有司机的getBusLineInfoByBusDriverAccount\n\r")
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