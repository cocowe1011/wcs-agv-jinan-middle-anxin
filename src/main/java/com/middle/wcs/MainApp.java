package com.middle.wcs;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author 文亮
 */
@SpringBootApplication
@Slf4j
@MapperScan("com.middle.wcs.*.dao")
@EnableScheduling
public class MainApp{
    public static void main(String[] args) {
        // 第一个参数是该类的名字.class 第二个参数是main方法中的参数
        ConfigurableApplicationContext context = SpringApplication.run(com.middle.wcs.MainApp.class, args);
        log.info("启动完毕");

        // 添加JVM关闭钩子，确保程序退出时能正确释放资源
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("应用程序即将关闭，执行清理工作...");
            
            try {
                // 强制刷新日志
                System.out.flush();
                System.err.flush();
                log.info("日志缓冲区已刷新");
                
                // 关闭Spring上下文
                if (context != null) {
                    context.close();
                    log.info("Spring上下文已关闭");
                }
            } catch (Exception e) {
                log.error("关闭过程中发生异常", e);
            }
            
            log.info("清理工作完成，应用程序安全退出");
        }));
    }
}
