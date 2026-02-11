package com.devcomrade.banbot.handler.callback.handler;

import com.devcomrade.banbot.components.IcuFormatters;
import com.devcomrade.banbot.handler.VoteType;
import com.devcomrade.banbot.handler.action.*;
import com.devcomrade.banbot.handler.context.CallbackContext;
import com.devcomrade.banbot.service.LocalizationService;
import com.devcomrade.banbot.service.RequestService;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Vote callback handler, handles vote buttons.
 */
abstract class VoteCallbackAbstract implements CallbackHandler {
    abstract RequestService requestService();
    abstract VoteType voteType();
    abstract LocalizationService localizationService();
    abstract BotAction pickerAction(Long chatId, Long userId, long untilDateEpochSeconds);
    /** i18n key for the confirmation message. */
    abstract String i18nKeyPrefix();
    abstract IcuFormatters pluralFormatter();

    public List<BotAction> handle(CallbackContext ctx) {
        var actions = new ArrayList<BotAction>();
        var chat = ctx.updateContext().chat();

        if (ctx.callbackData().parts().size() < 2) {
            return actions;
        }

        if (chat.getLocked() && !ctx.updateContext().isAdmin()) {
            return actions;
        }

        long candidateId;
        int reportedMessageId;
        try {
            candidateId = Long.parseLong(ctx.callbackData().parts().get(0));
            reportedMessageId = Integer.parseInt(ctx.callbackData().parts().get(1));
        } catch (NumberFormatException e) {
            return actions;
        }

        // Self vote is not allowed
        if (candidateId == ctx.voter().getId()) {
            return actions;
        }

        var reqOpt = requestService().findRequest(chat.getChatId(), candidateId, voteType());
        if (reqOpt.isEmpty())  {
            return actions;
        }

        var request = reqOpt.get();

        // Already voted
        if (request.getVoters() != null && request.getVoters().contains(ctx.voter().getId())) {
            return actions;
        }

        request = requestService().addVote(request, ctx.voter().getId());
        var candidateDisplay = requestService().getCandidateDisplay(request);

        if (request.getVotesCount() >= chat.getVoteLimit()) {
            actions.add(pickerAction(chat.getChatId(), candidateId, Instant.now().plusSeconds(chat.getMuteTime()).getEpochSecond()));

            // Delete vote button
            if (request.getMessageId() != null) {
                actions.add(new DeleteMessageAction(chat.getChatId(), Math.toIntExact(request.getMessageId())));
            }

            // Delete reported message
            if (reportedMessageId > 0) {
                actions.add(new DeleteMessageAction(chat.getChatId(), reportedMessageId));
            }

            // Confirmation message
            var time = pluralFormatter().formatDurationSeconds(chat.getMuteTime(), Locale.forLanguageTag(chat.getLanguage()), true);
            actions.add(new SendTextAction(chat.getChatId(), localizationService().getMessage(chat.getLanguage(), "%s_success".formatted(i18nKeyPrefix()), candidateDisplay, time)));
            requestService().deleteRequest(chat.getChatId(), candidateId, voteType());
        } else {
            // Update vote button and text
            var voteText = localizationService().getMessage(chat.getLanguage(), "%s_updated".formatted(i18nKeyPrefix()),
                    candidateDisplay, request.getVotesCount(), chat.getVoteLimit());

            var voteButtonText = localizationService().getMessage(chat.getLanguage(), "vote_button");
            var button = InlineKeyboardButton.builder()
                    .text(voteButtonText)
                    .callbackData(getKind() + "|" + candidateId + "|" + reportedMessageId)
                    .build();

            var markup = InlineKeyboardMarkup.builder().keyboardRow(new InlineKeyboardRow(button)).build();
            actions.add(new EditTextAction(chat.getChatId(), Math.toIntExact(request.getMessageId()), voteText, markup));
        }

        return actions;
    }
}
