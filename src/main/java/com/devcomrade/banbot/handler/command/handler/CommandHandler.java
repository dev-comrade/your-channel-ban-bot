package com.devcomrade.banbot.handler.command.handler;

import com.devcomrade.banbot.handler.action.BotAction;
import com.devcomrade.banbot.handler.context.CommandContext;
import com.mongodb.lang.Nullable;

import java.util.List;

public interface CommandHandler {
    String command();
    List<BotAction> handle(CommandContext ctx);

    default boolean supports(@Nullable String data) {
        return command().equalsIgnoreCase(data);
    }
}