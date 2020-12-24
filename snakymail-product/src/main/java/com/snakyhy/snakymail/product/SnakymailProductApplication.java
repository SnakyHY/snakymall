package com.snakyhy.snakymail.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@MapperScan("com.snakyhy.snakymail.product.dao")
@SpringBootApplication
public class SnakymailProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(SnakymailProductApplication.class, args);
    }

}
