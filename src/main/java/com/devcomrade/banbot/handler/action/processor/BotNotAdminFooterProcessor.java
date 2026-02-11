package com.devcomrade.banbot.handler.action.processor;

import com.devcomrade.banbot.handler.action.BotAction;
import com.devcomrade.banbot.handler.action.EditTextAction;
import com.devcomrade.banbot.handler.action.SendTextAction;
import com.devcomrade.banbot.model.Chat;
import com.devcomrade.banbot.service.ChatService;
import com.devcomrade.banbot.service.LocalizationService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class BotNotAdminFooterProcessor implements ActionPostProcessor {
    private final ChatService chatService;
    private final LocalizationService localizationService;

    // throttle: не чаще 1 раза в 60 минут на чат
    private final Cache<Long, Boolean> throttle = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(20))
            .maximumSize(10_000)
            .build();

    @Override
    public List<BotAction> process(List<BotAction> actions) {
        if (actions == null || actions.isEmpty()) return actions;

        // Extract chatId from actions
        var chatId = extractChatId(actions);
        if (chatId == null) return actions;

        var chat = chatService.getChat(chatId);
        if (chat != null && (chat.isBotAdmin() || chat.isUserChat())) {
            return actions;
        }

        // throttle
        if (throttle.getIfPresent(chatId) != null) {
            return actions;
        }

        throttle.put(chatId, Boolean.TRUE);

        // модифицируем только текстовые экшены
        var out = new ArrayList<BotAction>(actions.size());
        for (var a : actions) {
            if (a instanceof SendTextAction(
                    Long id, String text, InlineKeyboardMarkup replyMarkup, Consumer<Message> onSent
            )) {
                out.add(new SendTextAction(
                        id,
                        appendFooter(chat, text),
                        replyMarkup,
                        onSent
                ));
            } else if (a instanceof EditTextAction(
                    Long id, Integer messageId, String text, InlineKeyboardMarkup replyMarkup
            )) {
                out.add(new EditTextAction(
                        id,
                        messageId,
                        appendFooter(chat, text),
                        replyMarkup
                ));
            } else {
                out.add(a);
            }
        }
        return out;
    }

    private String appendFooter(Chat chat, String text) {
        if (text == null || text.isBlank()) {
            return text;
        }
        var footer = localizationService.getMessage(chat.getLanguage(), "not_admin_footer");
        // already contains footer
        if (text.contains(footer)) {
            return text;
        }

        return text + footer;
    }

    private Long extractChatId(List<BotAction> actions) {
        for (var a : actions) {
            if (a instanceof SendTextAction st) return st.chatId();
            if (a instanceof EditTextAction et) return et.chatId();
        }
        return null;
    }
}