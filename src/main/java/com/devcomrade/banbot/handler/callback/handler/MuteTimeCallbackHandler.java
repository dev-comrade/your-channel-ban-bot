package com.devcomrade.banbot.handler.callback.handler;

import com.devcomrade.banbot.components.BotIdentity;
import com.devcomrade.banbot.components.IcuFormatters;
import com.devcomrade.banbot.model.Chat;
import com.devcomrade.banbot.service.ChatService;
import com.devcomrade.banbot.service.LocalizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class MuteTimeCallbackHandler extends TwoIntSettingCallbackHandler {
    private final ChatService chatService;
    private final LocalizationService localizationService;
    private final IcuFormatters pluralFormatter;
    private final BotIdentity botIdentity;

    @Override
    public String getKind() {
        return "mute_time";
    }

    @Override
    protected LocalizationService localizationService() {
        return localizationService;
    }

    @Override
    protected String confirmKey() {
        return "mute_time_changed";
    }

    @Override
    protected Chat update(long chatId, int newValue) {
        return chatService.setMuteTime(chatId, newValue);
    }

    @Override
    protected String pluralForm(int newValue, String language, Locale locale) {
        return pluralFormatter.formatDurationSeconds(newValue, locale, true);
    }

    @Override
    protected BotIdentity botIdentity() {
        return botIdentity;
    }
}
