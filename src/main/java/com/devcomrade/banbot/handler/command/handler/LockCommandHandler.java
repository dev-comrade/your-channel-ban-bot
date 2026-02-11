package com.devcomrade.banbot.handler.command.handler;

import com.devcomrade.banbot.components.BotIdentity;
import com.devcomrade.banbot.handler.action.BotAction;
import com.devcomrade.banbot.handler.action.DeleteMessageAction;
import com.devcomrade.banbot.handler.action.SendTextAction;
import com.devcomrade.banbot.handler.context.CommandContext;
import com.devcomrade.banbot.service.ChatService;
import com.devcomrade.banbot.service.LocalizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LockCommandHandler implements CommandHandler {
    private final LocalizationService localizationService;
    private final ChatService chatService;
    private final BotIdentity botIdentity;

    @Override
    public String command() {
        return "lock";
    }

    @Override
    public List<BotAction> handle(CommandContext ctx) {
        var actions = new ArrayList<BotAction>();
        var chat = ctx.updateContext().chat();
        actions.add(new DeleteMessageAction(ctx.message().getChatId(), ctx.message().getMessageId()));

        // 1) only group chats
        if (chat.isUserChat()) {
            var text = localizationService.getMessage(chat.getLanguage(), "disabled_private_channel", command());
            actions.add(new SendTextAction(ctx.message().getChatId(), text));
        } else if (ctx.updateContext().isAdmin()) {
            var chatNew = chatService.setLocked(ctx.message().getChatId(), !chat.getLocked());
            var text = localizationService.getMessage(chatNew.getLanguage(), chatNew.getLocked() ? "lock_prompt" : "unlock_prompt", botIdentity.username());
            actions.add(new SendTextAction(ctx.message().getChatId(), text));
        }

        return actions;
    }
}
