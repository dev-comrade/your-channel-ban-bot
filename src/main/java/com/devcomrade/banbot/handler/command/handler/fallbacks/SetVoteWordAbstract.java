package com.devcomrade.banbot.handler.command.handler.fallbacks;

import com.devcomrade.banbot.components.BotIdentity;
import com.devcomrade.banbot.handler.VoteType;
import com.devcomrade.banbot.handler.action.BotAction;
import com.devcomrade.banbot.handler.action.DeleteMessageAction;
import com.devcomrade.banbot.handler.action.SendTextAction;
import com.devcomrade.banbot.handler.context.CommandContext;
import com.devcomrade.banbot.handler.command.handler.CommandHandler;
import com.devcomrade.banbot.handler.command.PendingVoteWord;
import com.devcomrade.banbot.handler.command.PendingVoteWordKey;
import com.devcomrade.banbot.service.LocalizationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

abstract class SetVoteWordAbstract implements CommandHandler {
    abstract VoteType voteType();
    abstract Map<PendingVoteWordKey, PendingVoteWord> pendingVoteWords();
    abstract LocalizationService localizationService();
    abstract void saveVoteWord(long chatId, String word);
    abstract BotIdentity botIdentity();

    protected String confirmationMessage() {
        return switch (voteType()) {
            case BAN -> "vote_ban_word_changed";
            case MUTE -> "vote_mute_word_changed";
        };
    }

    public List<BotAction> handle(CommandContext ctx) {
        var actions = new ArrayList<BotAction>();
        var chat = ctx.updateContext().chat();

        if (ctx.message().getReplyToMessage() == null) return actions;

        var key = new PendingVoteWordKey(chat.getChatId(), voteType());
        var pending = pendingVoteWords().get(key);
        if (pending == null) {
            return actions;
        }

        if (!ctx.updateContext().isAdmin()) {
            return actions;
        }

        if (ctx.message().getFrom() == null || ctx.message().getFrom().getId() != pending.adminUserId()) {
            return actions;
        }

        if (ctx.message().getReplyToMessage().getMessageId() != pending.promptMessageId()) {
            return actions;
        }

        var word = ctx.message().getText() == null ? "" : ctx.message().getText().trim();
        if (word.isEmpty()) {
            return actions;
        }

        saveVoteWord(chat.getChatId(), word);

        // cleanup prompt + user reply
        actions.add(new DeleteMessageAction(chat.getChatId(), pending.promptMessageId()));
        actions.add(new DeleteMessageAction(chat.getChatId(), ctx.message().getMessageId()));

        var confirm = localizationService().getMessage(chat.getLanguage(), confirmationMessage(), botIdentity().username(), word);
        actions.add(new SendTextAction(chat.getChatId(), confirm));

        pendingVoteWords().remove(key);

        return actions;
    }
}
