package com.devcomrade.banbot.components;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.GetMe;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.concurrent.atomic.AtomicReference;

@Component
@RequiredArgsConstructor
public class BotIdentity {
    private final TelegramClient telegramClient;
    private final AtomicReference<User> meRef = new AtomicReference<>();

    public User me() {
        var cached = meRef.get();
        if (cached != null) {
            return cached;
        }

        synchronized (this) {
            cached = meRef.get();
            if (cached != null) {
                return cached;
            }
            try {
                var me = telegramClient.execute(new GetMe());
                meRef.set(me);
                return me;
            } catch (TelegramApiException e) {
                throw new RuntimeException("Failed to call GetMe()", e);
            }
        }
    }

    public Long botId() {
        return me().getId();
    }

    public String username() {
        return me().getUserName();
    }
}