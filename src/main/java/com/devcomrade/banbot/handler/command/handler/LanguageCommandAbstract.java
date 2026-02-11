package com.devcomrade.banbot.handler.command.handler;

import com.devcomrade.banbot.handler.action.BotAction;
import com.devcomrade.banbot.handler.action.DeleteMessageAction;
import com.devcomrade.banbot.handler.action.SendTextAction;
import com.devcomrade.banbot.handler.context.CommandContext;
import com.devcomrade.banbot.service.LocalizationService;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.ArrayList;
import java.util.List;

abstract class LanguageCommandAbstract implements CommandHandler {
    abstract LocalizationService localizationService();
    // callback data format: lang|<code>|<originMsgId>|<mode>
    // mode: s = start (send welcome after), l = only language change
    abstract String mode();

    public boolean supports(String data) {
        return command().equalsIgnoreCase(data);
    }

    public List<BotAction> handle(CommandContext ctx) {
        var actions = new ArrayList<BotAction>();
        var chat = ctx.updateContext().chat();
        actions.add(new DeleteMessageAction(ctx.message().getChatId(), ctx.message().getMessageId()));

        if (ctx.updateContext().isAdmin()) {
            // origin message id is the user's /start message id (used for later cleanup)
            var originMsgId = ctx.message().getMessageId();
            var prompt = localizationService().getMessage(chat.getLanguage(), "language_prompt");
            var markup = buildLanguagePicker((long) originMsgId);
            actions.add(new SendTextAction(ctx.message().getChatId(), prompt, markup));
        }

        return actions;
    }

    public InlineKeyboardMarkup buildLanguagePicker(Long originMessageId) {
        var en = InlineKeyboardButton.builder()
                .text("English")
                .callbackData("lang|en|" + originMessageId + "|" + mode())
                .build();
        var ru = InlineKeyboardButton.builder()
                .text("Русский")
                .callbackData("lang|ru|" + originMessageId + "|" + mode())
                .build();

        return InlineKeyboardMarkup.builder().keyboardRow(new InlineKeyboardRow(en, ru)).build();
    }
}
