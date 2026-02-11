package com.devcomrade.banbot.handler.callback;

import com.devcomrade.banbot.handler.action.BotAction;
import com.devcomrade.banbot.handler.callback.handler.CallbackHandler;
import com.devcomrade.banbot.handler.context.CallbackContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CallbackDispatcher {
    private final List<CallbackHandler> handlers;

    public List<BotAction> dispatch(CallbackContext ctx) {
        for (var h : handlers) {
            if (h.supports(ctx.callbackData().kind())) {
                return h.handle(ctx);
            }
        }
        return List.of();
    }
}