package com.devcomrade.banbot.components;

import com.devcomrade.banbot.service.ChatAdminCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.message.InaccessibleMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramApiHelper {
    private final ChatAdminCacheService adminCache;

    public boolean isAdmin(Message message) {
        if (message.getChat().isUserChat()) {
            return true;
        }
        return getChatAdministrators(message.getChatId()).stream().anyMatch(admin -> admin.getUser().getId().equals(message.getFrom().getId()));
    }

    public boolean isAdmin(CallbackQuery callbackQuery) {
        // In private chats treat as admin to allow bot settings in DM if needed.
        var mi = callbackQuery.getMessage();
        if (mi instanceof Message m && m.getChat() != null && m.getChat().isUserChat()) {
            return true;
        }
        if (mi instanceof InaccessibleMessage im && im.getChat() != null && im.getChat().isUserChat()) {
            return true;
        }

        Long chatId;
        if (mi instanceof Message m) {
            chatId = m.getChatId();
        } else if (mi instanceof InaccessibleMessage im) {
            chatId = im.getChat().getId();
        } else {
            return false;
        }

        return getChatAdministrators(chatId).stream().anyMatch(admin -> admin.getUser().getId().equals(callbackQuery.getFrom().getId()));
    }

    public boolean isAdmin(long chatId, long userId) {
        return getChatAdministrators(chatId).stream().anyMatch(admin -> admin.getUser().getId().equals(userId));
    }

    @CacheEvict(cacheNames="chatAdmins", key="#chatId")
    public void invalidateChatAdministrators(long chatId) {
    }

    private List<ChatMember> getChatAdministrators(long chatId) {
        return adminCache.getChatAdministrators(chatId);
    }
}
