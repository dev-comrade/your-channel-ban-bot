package com.devcomrade.banbot.handler.command.handler;

import com.devcomrade.banbot.components.BotIdentity;
import com.devcomrade.banbot.components.IcuFormatters;
import com.devcomrade.banbot.handler.action.BotAction;
import com.devcomrade.banbot.handler.action.DeleteMessageAction;
import com.devcomrade.banbot.handler.action.SendTextAction;
import com.devcomrade.banbot.handler.context.CommandContext;
import com.devcomrade.banbot.service.LocalizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class HelpCommandHandler implements CommandHandler {
    private final LocalizationService localizationService;
    private final IcuFormatters pluralFormatter;
    private final BotIdentity botIdentity;

    @Override
    public String command() {
        return "help";
    }

    @Override
    public List<BotAction> handle(CommandContext ctx) {
        var actions = new ArrayList<BotAction>();
        var chat = ctx.updateContext().chat();

        actions.add(new DeleteMessageAction(ctx.message().getChatId(), ctx.message().getMessageId()));

        String welcome;
        if (chat.isUserChat()) {
            welcome = localizationService.getMessage(chat.getLanguage(), "welcome_private", botIdentity.username());
        } else {
            welcome = localizationService.getMessage(chat.getLanguage(), "welcome", botIdentity.username(), (chat.getLocked() ? "ON" : "OFF"), pluralFormatter.formatVotes(chat.getVoteLimit(), Locale.forLanguageTag(chat.getLanguage()), chat.getLanguage()), pluralFormatter.formatDurationSeconds(chat.getMuteTime(), Locale.forLanguageTag(chat.getLanguage())), chat.getVoteBanWord(), chat.getVoteMuteWord());
        }

        if (ctx.updateContext().isAdmin()) {
            actions.add(new SendTextAction(ctx.message().getChatId(), welcome));
        }

        return actions;
    }
}
