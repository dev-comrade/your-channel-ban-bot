package com.devcomrade.banbot.handler.context;

import com.devcomrade.banbot.components.BotIdentity;
import com.devcomrade.banbot.components.TelegramApiHelper;
import com.devcomrade.banbot.handler.action.SendTextAction;
import com.devcomrade.banbot.handler.action.executor.SendTextExecutor;
import com.devcomrade.banbot.service.ChatService;
import com.devcomrade.banbot.service.LocalizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.InaccessibleMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@RequiredArgsConstructor
public class UpdateContextResolver {
    private final ChatService chatService;
    private final TelegramApiHelper telegramApiHelper;
    private final BotIdentity botIdentity;
    private final SendTextExecutor sendTextExecutor;
    private final LocalizationService localizationService;

    public UpdateContext resolve(Update update) {
        var chatId = extractChatId(update);
        var isUserChat = extractIsUserChat(update);
        var isAdmin = extractIsAdmin(update);
        var chat = chatService.getOrCreateChat(chatId);
        var isBotAdmin = false;
        // Do not check admin status for private chats
        if (!isUserChat) {
            isBotAdmin = telegramApiHelper.isAdmin(chatId, botIdentity.botId());
        }

        var changed = false;

        if (isBotAdmin != chat.isBotAdmin()) {
            telegramApiHelper.invalidateChatAdministrators(chatId);
            changed = true;
            chat.setBotAdmin(isBotAdmin);
            if (isBotAdmin) {
                try {
                    var sendText = new SendTextAction(chatId, localizationService.getMessage(chat.getLanguage(), "admin_promotion_notification"));
                    sendTextExecutor.execute(sendText);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        if (isUserChat != chat.isUserChat()) {
            changed = true;
            chat.setUserChat(isUserChat);
        }

        if (changed) {
            chatService.updateChat(chat);
        }

        return new UpdateContext(chatId, chat, isAdmin);
    }

    private long extractChatId(Update update) {
        if (update.hasMyChatMember()) {
            return update.getMyChatMember().getChat().getId();
        }
        if (update.hasMessage()) {
            return update.getMessage().getChatId();
        }
        if (update.hasCallbackQuery()) {
            var mi = update.getCallbackQuery().getMessage();
            if (mi instanceof Message m) return m.getChatId();
            if (mi instanceof InaccessibleMessage im) return im.getChat().getId();
        }
        throw new IllegalStateException("Can't extract chatId from update");
    }

    private boolean extractIsUserChat(Update update) {
        if (update.hasMyChatMember()) {
            return update.getMyChatMember().getChat().isUserChat();
        }
        if (update.hasMessage()) {
            return update.getMessage().getChat().isUserChat();
        }
        if (update.hasCallbackQuery()) {
            var mi = update.getCallbackQuery().getMessage();
            if (mi instanceof Message m) return m.getChat().isUserChat();
            if (mi instanceof InaccessibleMessage im) return im.getChat().isUserChat();
        }
        throw new IllegalStateException("Can't extract chatId from update");
    }

    private boolean extractIsAdmin(Update update) {
        if (update.hasMessage()) {
            return telegramApiHelper.isAdmin(update.getMessage());
        }
        if (update.hasCallbackQuery()) {
            return telegramApiHelper.isAdmin(update.getCallbackQuery());
        }
        // Promote to admin can only admin
        if (update.hasMyChatMember()) {
            return true;
        }
        throw new IllegalStateException("Can't extract chatId from update");
    }
}