package com.devcomrade.banbot.handler.callback;

import com.devcomrade.banbot.handler.action.BotAction;
import com.devcomrade.banbot.handler.context.CallbackContext;
import com.devcomrade.banbot.handler.context.UpdateContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CallbackFacade {
    private final CallbackDispatcher callbackDispatcher;

    public List<BotAction> handleCallbackQuery(CallbackQuery callbackQuery, UpdateContext updateContext) {
        var actions = new ArrayList<BotAction>();

        var data = callbackQuery.getData();
        if (data == null) {
            return actions;
        }

        var mi = callbackQuery.getMessage();
        if (mi == null) {
            return actions;
        }

        var ctx = new CallbackContext(CallbackData.parse(data), updateContext, callbackQuery.getFrom(), mi);
        return callbackDispatcher.dispatch(ctx);
    }
}
