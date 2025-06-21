package com.muying.weblog.web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

@SpringBootApplication
@ComponentScan(basePackages = {"com.muying.weblog.*"}) // 多模块项目中，必需手动指定扫描 com.muying 包下面的所有类
@MapperScan("com.muying.weblog.common.mapper")  // 必须与UserMapper实际包路径一致
@EnableScheduling // 启用定时任务
public class WeblogWebApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(WeblogWebApplication.class, args);
    }

}
