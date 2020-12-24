package com.snakyhy.snakymail.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class SnakymailOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(SnakymailOrderApplication.class, args);
    }

}
