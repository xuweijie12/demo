package com.xwj;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j  /*加多了这个注解就可以直接使用日志 log*/
@SpringBootApplication
@ServletComponentScan   /*要加这个注解才会去扫描过滤器 带@webFilter的过滤器*/
@EnableTransactionManagement  /*事物统一*/
public class ReggieApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReggieApplication.class,args);
        log.info("启动成功...");
    }
}
