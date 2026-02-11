package com.devcomrade.banbot.handler.action;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

public record EditTextAction(Long chatId, Integer messageId, String text, InlineKeyboardMarkup replyMarkup) implements BotAction {
}
