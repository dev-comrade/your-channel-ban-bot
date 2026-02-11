package com.devcomrade.banbot.handler.context;

import com.devcomrade.banbot.model.Chat;

public record UpdateContext(
        long chatId,
        Chat chat,
        boolean isAdmin
) {}