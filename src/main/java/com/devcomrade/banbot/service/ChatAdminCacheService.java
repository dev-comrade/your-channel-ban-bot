package com.devcomrade.banbot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatAdministrators;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatAdminCacheService {
    private final TelegramClient telegramClient;

    @Cacheable(cacheNames = "chatAdmins", key = "#chatId", sync = true, unless = "#result == null || #result.isEmpty()")
    public List<ChatMember> getChatAdministrators(long chatId) {
        try {
            return telegramClient.execute(GetChatAdministrators.builder().chatId(chatId).build());
        } catch (TelegramApiException e) {
            log.error("Error getting chat administrators", e);
            return List.of();
        }
    }
}
