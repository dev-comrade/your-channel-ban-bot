package com.devcomrade.banbot.handler.command.handler.fallbacks;

import com.devcomrade.banbot.components.IcuFormatters;
import com.devcomrade.banbot.handler.VoteType;
import com.devcomrade.banbot.handler.action.BotAction;
import com.devcomrade.banbot.handler.action.EditTextAction;
import com.devcomrade.banbot.handler.action.SendTextAction;
import com.devcomrade.banbot.handler.context.CommandContext;
import com.devcomrade.banbot.handler.command.handler.CommandHandler;
import com.devcomrade.banbot.service.LocalizationService;
import com.devcomrade.banbot.service.RequestService;
import com.devcomrade.banbot.service.UserService;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Initiate vote command
 */
public abstract class VoteCommandAbstract implements CommandHandler {
    abstract VoteType voteType();
    /** i18n key for the confirmation message. */
    abstract String i18nKeyPrefix();
    abstract LocalizationService localizationService();
    abstract RequestService requestService();
    abstract UserService userService();
    abstract BotAction createAction(long chatId, long userId, long untilDateEpochSeconds);
    abstract IcuFormatters pluralFormatter();

    public List<BotAction> handle(CommandContext ctx) {
        var actions = new ArrayList<BotAction>();
        var chat = ctx.updateContext().chat();

        if (ctx.message().getReplyToMessage() == null) {
            return actions;
        }

        if (chat.getLocked() && !ctx.updateContext().isAdmin()) {
            return actions;
        }

        var candidate = ctx.message().getReplyToMessage().getFrom();
        if (candidate == null || candidate.getIsBot()) {
            return actions;
        }

        var chatId = chat.getChatId();
        var candidateId = candidate.getId();

        // Self vote is not allowe
        if (candidateId.equals(ctx.message().getFrom().getId())) {
            return actions;
        }

        var time = pluralFormatter().formatDurationSeconds(chat.getMuteTime(), Locale.forLanguageTag(chat.getLanguage()), true);

        var existingRequest = requestService().findRequest(chatId, candidateId, voteType());
        if (existingRequest.isPresent()) {
            var request = existingRequest.get();
            var voterId = ctx.message().getFrom().getId();
            if (!request.getVoters().contains(voterId)) {
                request = requestService().addVote(request, voterId);
            }

            if (request.getVotesCount() >= chat.getVoteLimit()) {
                actions.add(createAction(chatId, candidateId, Instant.now().plusSeconds(chat.getMuteTime()).getEpochSecond()));

                var userDisplay = userService().getUserDisplay(candidate);
                var successText = localizationService().getMessage(chat.getLanguage(), "%s_success".formatted(i18nKeyPrefix()), userDisplay, time);
                actions.add(new SendTextAction(chatId, successText));
                requestService().deleteRequest(chatId, candidateId, voteType());
            } else {
                requestService().createRequest(chatId, candidate, request.getMessageId(), true, voteType());
                var userDisplay = userService().getUserDisplay(candidate);
                var voteText = localizationService().getMessage(chat.getLanguage(), "%s_updated".formatted(i18nKeyPrefix()),
                        userDisplay, request.getVotesCount(), chat.getVoteLimit());

                var voteButtonText = localizationService().getMessage(chat.getLanguage(), "vote_button");
                var button = InlineKeyboardButton.builder()
                        .text(voteButtonText)
                        .callbackData(i18nKeyPrefix() + "|" + candidateId + "|" + ctx.message().getMessageId())
                        .build();
                var markup = InlineKeyboardMarkup.builder().keyboardRow(new InlineKeyboardRow(button)).build();
                actions.add(new EditTextAction(chatId, Math.toIntExact(request.getMessageId()), voteText, markup));
            }

            return actions;
        }

        // No existing request
        if (chat.getVoteLimit() <= 1) {
            actions.add(createAction(chatId, candidateId, Instant.now().plusSeconds(chat.getMuteTime()).getEpochSecond()));

            var userDisplay = userService().getUserDisplay(candidate);
            var successText = localizationService().getMessage(chat.getLanguage(), "%s_success".formatted(i18nKeyPrefix()), userDisplay, time);
            actions.add(new SendTextAction(chatId, successText));
            return actions;
        }

        // Create new request
        var userDisplay = userService().getUserDisplay(candidate);
        var voteText = localizationService().getMessage(chat.getLanguage(), "%s_started".formatted(i18nKeyPrefix()),
                userDisplay, 1, chat.getVoteLimit());

        var voteButtonText = localizationService().getMessage(chat.getLanguage(), "vote_button");
        var button = InlineKeyboardButton.builder()
                .text(voteButtonText)
                .callbackData(i18nKeyPrefix() + "|" + candidateId + "|" + ctx.message().getMessageId())
                .build();
        var markup = InlineKeyboardMarkup.builder().keyboardRow(new InlineKeyboardRow(button)).build();

        actions.add(new SendTextAction(chatId, voteText, markup, sentMsg -> {
            var request = requestService().createRequest(chatId, candidate, (long) sentMsg.getMessageId(), false, voteType());
            requestService().addVote(request, ctx.message().getFrom().getId());
        }));

        return actions;
    }
}
