package com.devcomrade.banbot.handler.action;

import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.function.Consumer;

public record SendTextAction(Long chatId, String text, InlineKeyboardMarkup replyMarkup, Consumer<Message> onSent) implements BotAction {
    public SendTextAction(Long chatId, String text) {
        this(chatId, text, null, null);
    }

    public SendTextAction(Long chatId, String text, InlineKeyboardMarkup replyMarkup) {
        this(chatId, text, replyMarkup, null);
    }
}