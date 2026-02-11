package com.devcomrade.banbot.handler.context;

import org.telegram.telegrambots.meta.api.objects.message.Message;

public record CommandContext(
        Message message,
        String command,
        String argsRaw,
        UpdateContext updateContext
) {}
