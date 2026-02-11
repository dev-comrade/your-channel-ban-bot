package com.devcomrade.banbot.handler.command.handler;

import com.devcomrade.banbot.service.LocalizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BotAddedToChatCommandHandler extends LanguageCommandAbstract {
    private final LocalizationService localizationService;

    @Override
    public String command() {
        return "bot_added_to_chat";
    }

    @Override
    protected LocalizationService localizationService() {
        return localizationService;
    }

    @Override
    protected String mode() {
        return "s";
    }
}
