package com.devcomrade.banbot.handler.action;

public record MuteMemberAction(Long chatId, Long userId, long untilDateEpochSeconds) implements BotAction {}