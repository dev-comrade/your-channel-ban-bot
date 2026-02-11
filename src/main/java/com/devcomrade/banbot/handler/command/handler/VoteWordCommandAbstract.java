package com.devcomrade.banbot.handler.command.handler;

import com.devcomrade.banbot.handler.VoteType;
import com.devcomrade.banbot.handler.action.BotAction;
import com.devcomrade.banbot.handler.action.DeleteMessageAction;
import com.devcomrade.banbot.handler.action.SendTextAction;
import com.devcomrade.banbot.handler.context.CommandContext;
import com.devcomrade.banbot.handler.command.PendingVoteWord;
import com.devcomrade.banbot.handler.command.PendingVoteWordKey;
import com.devcomrade.banbot.model.Chat;
import com.devcomrade.banbot.service.LocalizationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class VoteWordCommandAbstract implements CommandHandler {
    abstract LocalizationService localizationService();
    abstract VoteType voteType();
    abstract Map<PendingVoteWordKey, PendingVoteWord> pendingVoteWords();
    abstract String currentCharWord(Chat chat);

    public List<BotAction> handle(CommandContext ctx) {
        var actions = new ArrayList<BotAction>();
        var chat = ctx.updateContext().chat();

        if (!ctx.updateContext().isAdmin()) {
            return actions;
        }

        actions.add(new DeleteMessageAction(ctx.message().getChatId(), ctx.message().getMessageId()));
        // only group chats
        if (chat.isUserChat()) {
            var message = localizationService().getMessage(chat.getLanguage(), "disabled_private_channel", command());
            actions.add(new SendTextAction(ctx.message().getChatId(), message));
        } else {
            var current = currentCharWord(chat);
            var key = new PendingVoteWordKey(ctx.message().getChatId(), voteType());

            // cleanup previous pending prompt in this chat (best-effort)
            var prev = pendingVoteWords().remove(key);
            if (prev != null) {
                actions.add(new DeleteMessageAction(ctx.message().getChatId(), prev.promptMessageId()));
            }

            var prompt = localizationService().getMessage(chat.getLanguage(), "%s_prompt".formatted(command()), current);
            actions.add(new SendTextAction(ctx.message().getChatId(), prompt, null, sentMsg -> pendingVoteWords().put(key, new PendingVoteWord(ctx.message().getFrom().getId(), sentMsg.getMessageId()))));
        }

        return actions;
    }

}
