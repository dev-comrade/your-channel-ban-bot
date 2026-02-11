package com.devcomrade.banbot.handler.callback.handler;

import com.devcomrade.banbot.handler.action.BotAction;
import com.devcomrade.banbot.handler.context.CallbackContext;
import com.mongodb.lang.Nullable;

import java.util.List;

public interface CallbackHandler {
    String getKind();

    List<BotAction> handle(CallbackContext ctx);

    default boolean supports(@Nullable String data) {
        return getKind().equalsIgnoreCase(data);
    }
}
