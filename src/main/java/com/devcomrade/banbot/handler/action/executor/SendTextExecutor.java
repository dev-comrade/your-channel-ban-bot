package com.devcomrade.banbot.handler.action.executor;

import com.devcomrade.banbot.handler.action.SendTextAction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
@RequiredArgsConstructor
public class SendTextExecutor implements ActionExecutor<SendTextAction> {
    private final TelegramClient telegramClient;

    @Override
    public Class<SendTextAction> supports() {
        return SendTextAction.class;
    }

    @Override
    public void execute(SendTextAction a) throws TelegramApiException {
        var sm = SendMessage.builder()
                .chatId(a.chatId())
                .text(a.text())
                .parseMode("HTML")
                .replyMarkup(a.replyMarkup())
                .build();

        var sent = telegramClient.execute(sm);
        if (a.onSent() != null) {
            a.onSent().accept(sent);
        }
    }
}