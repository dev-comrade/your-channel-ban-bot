package com.devcomrade.banbot.config;

import lombok.NonNull;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@EnableConfigurationProperties(TelegramBotProperties.class)
@Configuration
public class TelegramConfig {
    @Bean
    public @NonNull TelegramClient telegramClient(TelegramBotProperties props) {
        return new OkHttpTelegramClient(props.getToken());
    }
}
