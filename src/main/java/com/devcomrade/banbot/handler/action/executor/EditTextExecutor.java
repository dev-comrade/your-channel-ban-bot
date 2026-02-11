package com.devcomrade.banbot.handler.action.executor;

import com.devcomrade.banbot.handler.action.EditTextAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class EditTextExecutor implements ActionExecutor<EditTextAction> {
    private final TelegramClient telegramClient;

    @Override
    public Class<EditTextAction> supports() {
        return EditTextAction.class;
    }

    @Override
    public void execute(EditTextAction action) {
        try {
            telegramClient.execute(EditMessageText.builder()
                    .chatId(action.chatId())
                    .messageId(action.messageId())
                    .text(action.text())
                    .parseMode("HTML")
                    .replyMarkup(action.replyMarkup())
                    .build());
        } catch (TelegramApiException e) {
            log.warn("Failed to edit message {} in chat {}", action.messageId(), action.chatId());
        }
    }
}
