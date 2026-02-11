package com.devcomrade.banbot.handler.callback.handler;

import com.devcomrade.banbot.components.BotIdentity;
import com.devcomrade.banbot.components.IcuFormatters;
import com.devcomrade.banbot.handler.action.BotAction;
import com.devcomrade.banbot.handler.action.DeleteMessageAction;
import com.devcomrade.banbot.handler.action.SendTextAction;
import com.devcomrade.banbot.handler.context.CallbackContext;
import com.devcomrade.banbot.service.ChatService;
import com.devcomrade.banbot.service.LocalizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class LanguageCallbackHandler implements CallbackHandler {
    private final ChatService chatService;
    private final LocalizationService localizationService;
    private final IcuFormatters pluralFormatter;
    private final BotIdentity botIdentity;

    @Override
    public String getKind() {
        return "lang";
    }

    @Override
    public List<BotAction> handle(CallbackContext ctx) {
        var actions = new ArrayList<BotAction>();
        var chat = ctx.updateContext().chat();
        if (ctx.callbackData().parts().size() < 3) {
            return actions;
        }

        if (!ctx.updateContext().isAdmin()) {
            return actions;
        }

        var lang = ctx.callbackData().parts().get(0);
        long originMsgId;
        try {
            originMsgId = Long.parseLong(ctx.callbackData().parts().get(1));
        } catch (NumberFormatException e) {
            originMsgId = 0L;
        }
        var startFlow = "s".equals(ctx.callbackData().parts().get(2));
        // apply language
        var chatNew = chatService.setLanguage(chat.getChatId(), lang);

        // cleanup: delete picker + origin message (if present)
        actions.add(new DeleteMessageAction(chatNew.getChatId(), ctx.messageId()));
        if (originMsgId > 0) {
            actions.add(new DeleteMessageAction(chatNew.getChatId(), (int) originMsgId));
        }

        // confirmation
        var confirm = localizationService.getMessage(lang, "language_changed", botIdentity.username());
        actions.add(new SendTextAction(chatNew.getChatId(), confirm));

        // welcome (only for start/bot-added flow)
        if (startFlow) {
            ctx.message().ifPresent(msg -> {
                String welcome;
                if (chatNew.isUserChat()) {
                    welcome = localizationService.getMessage(lang, "welcome_private", botIdentity.username());
                } else {
                    welcome = localizationService.getMessage(chat.getLanguage(), "welcome", botIdentity.username(), (chat.getLocked() ? "ON" : "OFF"), pluralFormatter.formatVotes(chat.getVoteLimit(), Locale.forLanguageTag(chat.getLanguage()), chat.getLanguage()), pluralFormatter.formatDurationSeconds(chat.getMuteTime(), Locale.forLanguageTag(chat.getLanguage())), chat.getVoteBanWord(), chat.getVoteMuteWord());
                }
                actions.add(new DeleteMessageAction(chatNew.getChatId(), msg.getMessageId()));
                actions.add(new SendTextAction(chatNew.getChatId(), welcome));
            });
        }

        return actions;
    }
}
