package com.devcomrade.banbot.handler.command.handler;

import com.devcomrade.banbot.components.IcuFormatters;
import com.devcomrade.banbot.handler.action.BotAction;
import com.devcomrade.banbot.handler.action.DeleteMessageAction;
import com.devcomrade.banbot.handler.action.SendTextAction;
import com.devcomrade.banbot.handler.context.CommandContext;
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
public class LimitCommandHandler implements CommandHandler {
    private final LocalizationService localizationService;
    private final IcuFormatters pluralFormatter;

    @Override
    public String command() {
        return "limit";
    }

    @Override
    public List<BotAction> handle(CommandContext ctx) {
        var actions = new ArrayList<BotAction>();
        var chat = ctx.updateContext().chat();
        actions.add(new DeleteMessageAction(ctx.message().getChatId(), ctx.message().getMessageId()));

        if (chat.isUserChat()) {
            var text = localizationService.getMessage(chat.getLanguage(), "disabled_private_channel", "limit");
            actions.add(new SendTextAction(ctx.message().getChatId(), text));
        } else if (ctx.updateContext().isAdmin()) {
            var prompt = localizationService.getMessage(chat.getLanguage(), "limit_prompt", pluralFormatter.formatVotes(chat.getVoteLimit(), Locale.forLanguageTag(chat.getLanguage()), chat.getLanguage()));
            var markup = buildLimitPicker(ctx.message().getMessageId());
            actions.add(new SendTextAction(ctx.message().getChatId(), prompt, markup));
        }

        return actions;
    }

    private InlineKeyboardMarkup buildLimitPicker(int originMessageId) {
        // callback data format: limit|<value>|<originMsgId>
        var b1 = InlineKeyboardButton.builder().text("1").callbackData("limit|1|" + originMessageId).build();
        var b3 = InlineKeyboardButton.builder().text("3").callbackData("limit|3|" + originMessageId).build();
        var b5 = InlineKeyboardButton.builder().text("5").callbackData("limit|5|" + originMessageId).build();

        var b10 = InlineKeyboardButton.builder().text("10").callbackData("limit|10|" + originMessageId).build();
        var b20 = InlineKeyboardButton.builder().text("20").callbackData("limit|20|" + originMessageId).build();
        var b30 = InlineKeyboardButton.builder().text("30").callbackData("limit|30|" + originMessageId).build();

        var row1 = new InlineKeyboardRow(b1, b3, b5);
        var row2 = new InlineKeyboardRow(b10, b20, b30);

        return InlineKeyboardMarkup.builder().keyboardRow(row1).keyboardRow(row2).build();
    }
}
