package com.devcomrade.banbot.handler.action;

public record DeleteMessageAction(Long chatId, Integer messageId) implements BotAction {
}
