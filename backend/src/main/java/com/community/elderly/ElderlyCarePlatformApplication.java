package com.community.elderly;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.community.elderly.mapper")
public class ElderlyCarePlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElderlyCarePlatformApplication.class, args);
    }

}
