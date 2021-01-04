package com.snakyhy.snakymail.ware;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@EnableFeignClients(basePackages = "com.snakyhy.snakymail.ware.feign")
@EnableDiscoveryClient
@SpringBootApplication
public class SnakymailWareApplication {

    public static void main(String[] args) {
        SpringApplication.run(SnakymailWareApplication.class, args);
    }

}
