package com.devcomrade.banbot.handler.action.executor;

import com.devcomrade.banbot.handler.action.DeleteMessageAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteMessageExecutor implements ActionExecutor<DeleteMessageAction> {
    private final TelegramClient telegramClient;

    @Override
    public Class<DeleteMessageAction> supports() {
        return DeleteMessageAction.class;
    }

    @Override
    public void execute(DeleteMessageAction a) {
        try {
            telegramClient.execute(DeleteMessage.builder()
                    .chatId(a.chatId())
                    .messageId(a.messageId())
                    .build());
        } catch (TelegramApiException e) {
            log.warn("Failed to delete message {} in chat {}", a.messageId(), a.chatId());
        }
    }
}