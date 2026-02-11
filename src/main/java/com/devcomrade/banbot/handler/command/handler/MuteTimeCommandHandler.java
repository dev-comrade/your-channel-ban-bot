package com.devcomrade.banbot.handler.command.handler;

import com.devcomrade.banbot.components.IcuFormatters;
import com.devcomrade.banbot.handler.action.BotAction;
import com.devcomrade.banbot.handler.action.DeleteMessageAction;
import com.devcomrade.banbot.handler.action.SendTextAction;
import com.devcomrade.banbot.handler.context.CommandContext;
import com.devcomrade.banbot.model.Chat;
import com.devcomrade.banbot.service.LocalizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


@Component
@RequiredArgsConstructor
public class MuteTimeCommandHandler implements CommandHandler {
    private final LocalizationService localizationService;
    private final IcuFormatters pluralFormatter;

    @Override
    public String command() {
        return "mute_time";
    }

    @Override
    public List<BotAction> handle(CommandContext ctx) {
        var actions = new ArrayList<BotAction>();
        var chat = ctx.updateContext().chat();
        actions.add(new DeleteMessageAction(ctx.message().getChatId(), ctx.message().getMessageId()));

        if (chat.isUserChat()) {
            var text = localizationService.getMessage(chat.getLanguage(), "disabled_private_channel", command());
            actions.add(new SendTextAction(ctx.message().getChatId(), text));
        } else if (ctx.updateContext().isAdmin()) {
            var prompt = localizationService.getMessage(chat.getLanguage(), "mute_time_prompt", pluralFormatter.formatDurationSeconds(chat.getMuteTime(), Locale.forLanguageTag(chat.getLanguage())));
            var markup = buildTimePicker(ctx.message().getMessageId(), chat);

            // show picker + clean up command message (best-effort)
            actions.add(new SendTextAction(ctx.message().getChatId(), prompt, markup));
        }
        return actions;
    }

    private InlineKeyboardMarkup buildTimePicker(int originMessageId, Chat chat) {
        // callback data format: time|<value>|<originMsgId>
        var b1 = InlineKeyboardButton.builder().text(localizationService.getMessage(chat.getLanguage(), "mute_time_1")).callbackData("mute_time|60|" + originMessageId).build();
        var b5 = InlineKeyboardButton.builder().text(localizationService.getMessage(chat.getLanguage(), "mute_time_5")).callbackData("mute_time|300|" + originMessageId).build();
        var b10 = InlineKeyboardButton.builder().text(localizationService.getMessage(chat.getLanguage(), "mute_time_10")).callbackData("mute_time|600|" + originMessageId).build();

        var b30 = InlineKeyboardButton.builder().text(localizationService.getMessage(chat.getLanguage(), "mute_time_30")).callbackData("mute_time|1800|" + originMessageId).build();
        var b60 = InlineKeyboardButton.builder().text(localizationService.getMessage(chat.getLanguage(), "mute_time_60")).callbackData("mute_time|1800|" + originMessageId).build();
        var b180 = InlineKeyboardButton.builder().text(localizationService.getMessage(chat.getLanguage(), "mute_time_180")).callbackData("mute_time|10800|" + originMessageId).build();

        return InlineKeyboardMarkup.builder()
                .keyboardRow(new InlineKeyboardRow(b1, b5, b10))
                .keyboardRow(new InlineKeyboardRow(b30, b60, b180))
                .build();
    }
}
