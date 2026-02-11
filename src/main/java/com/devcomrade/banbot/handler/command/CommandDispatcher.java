package com.devcomrade.banbot.handler.command;

import com.devcomrade.banbot.handler.action.BotAction;
import com.devcomrade.banbot.handler.command.handler.CommandHandler;
import com.devcomrade.banbot.handler.context.CommandContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CommandDispatcher {
    private final List<CommandHandler> handlers;

    public List<BotAction> dispatch(CommandContext ctx) {
        if (ctx.command() == null) {
            return List.of();
        }

        for (var h : handlers) {
            if (h.supports(ctx.command())) {
                return h.handle(ctx);
            }
        }
        return List.of();
    }
}