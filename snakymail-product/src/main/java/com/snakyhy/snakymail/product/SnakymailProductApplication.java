package com.snakyhy.snakymail.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableRedisHttpSession
@EnableFeignClients(basePackages = "com.snakyhy.snakymail.product.feign")
@EnableDiscoveryClient
@MapperScan("com.snakyhy.snakymail.product.dao")
@SpringBootApplication
public class SnakymailProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(SnakymailProductApplication.class, args);
    }

}
