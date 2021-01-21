package com.snakyhy.snakymall.cart.config;

import com.snakyhy.snakymall.cart.interceptor.CartInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SnakyMallWebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        CartInterceptor cartInterceptor = new CartInterceptor();
        registry.addInterceptor(cartInterceptor).addPathPatterns("/**");

    }
}
