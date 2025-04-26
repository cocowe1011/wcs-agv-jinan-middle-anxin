package com.middle.wcs;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author 文亮
 */
@SpringBootApplication
@Slf4j
@MapperScan("com.middle.ccs.*.dao")
@EnableScheduling
public class MainApp{
    public static void main(String[] args) {
        // 第一个参数是该类的名字.class 第二个参数是main方法中的参数
        SpringApplication.run(com.middle.wcs.MainApp.class, args);
        log.info("启动完毕");
    }
}
