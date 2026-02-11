package com.devcomrade.banbot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class ChannelGuardApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChannelGuardApplication.class, args);
    }
}
