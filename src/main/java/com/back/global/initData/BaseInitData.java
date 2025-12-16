package com.back.global.initData;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BaseInitData {

    @Bean
    public ApplicationRunner baseInitDataRunner (){
        return args->{
            System.out.println("ApplicationRunner 빈은 스프링에 등록되면 자동으로 실행됩니다");
        };
    }
}