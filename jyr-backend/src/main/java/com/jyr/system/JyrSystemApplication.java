package com.jyr.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class JyrSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(JyrSystemApplication.class, args);
    }
}
