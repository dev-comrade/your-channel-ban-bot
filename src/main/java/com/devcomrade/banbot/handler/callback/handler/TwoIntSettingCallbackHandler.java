package com.devcomrade.banbot.handler.callback.handler;

import com.devcomrade.banbot.components.BotIdentity;
import com.devcomrade.banbot.handler.action.BotAction;
import com.devcomrade.banbot.handler.action.DeleteMessageAction;
import com.devcomrade.banbot.handler.action.SendTextAction;
import com.devcomrade.banbot.handler.context.CallbackContext;
import com.devcomrade.banbot.model.Chat;
import com.devcomrade.banbot.service.LocalizationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Template for simple setting callbacks where callback_data is:
 * <kind>|<value>|<originMsgId>
 * and the only variation between handlers is the update method and confirm i18n key.
 */
public abstract class TwoIntSettingCallbackHandler implements CallbackHandler {
    abstract LocalizationService localizationService();
    /** i18n key for the confirmation message. */
    abstract String confirmKey();
    /** Apply the new value and return updated chat settings. */
    abstract Chat update(long chatId, int newValue);
    abstract String pluralForm(int newValue, String language, Locale locale);
    abstract BotIdentity botIdentity();

    @Override
    public List<BotAction> handle(CallbackContext ctx) {
        var actions = new ArrayList<BotAction>();
        var chat = ctx.updateContext().chat();
        // Admin-only setting change. Non-admin clicks are ignored.
        if (!ctx.updateContext().isAdmin()) {
            return actions;
        }

        // Expect exactly 2 parts: value + originMsgId
        if (ctx.callbackData().parts().size() < 2) {
            actions.add(new DeleteMessageAction(chat.getChatId(), ctx.messageId()));
            return actions;
        }

        int newValue;
        int originMsgId;
        try {
            newValue = Integer.parseInt(ctx.callbackData().parts().get(0));
            originMsgId = Integer.parseInt(ctx.callbackData().parts().get(1));
        } catch (NumberFormatException e) {
            actions.add(new DeleteMessageAction(chat.getChatId(), ctx.messageId()));
            return actions;
        }

        // cleanup: origin command message (if still exists)
        if (originMsgId > 0) {
            actions.add(new DeleteMessageAction(chat.getChatId(), originMsgId));
        }

        var chatNew = update(chat.getChatId(), newValue);
        var confirm = localizationService().getMessage(chat.getLanguage(), confirmKey(), botIdentity().username(), pluralForm(newValue, chat.getLanguage(), Locale.forLanguageTag(chat.getLanguage())));
        actions.add(new SendTextAction(chatNew.getChatId(), confirm));

        // cleanup: delete picker itself
        actions.add(new DeleteMessageAction(chatNew.getChatId(), ctx.messageId()));
        return actions;
    }
}
