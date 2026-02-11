package com.devcomrade.banbot.handler.action;

public record BanMemberAction(Long chatId, Long userId, long untilDateEpochSeconds) implements BotAction {
}
