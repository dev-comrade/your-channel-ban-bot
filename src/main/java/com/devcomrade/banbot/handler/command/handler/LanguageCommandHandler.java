package com.devcomrade.banbot.handler.command.handler;

import com.devcomrade.banbot.service.LocalizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LanguageCommandHandler extends LanguageCommandAbstract {
    private final LocalizationService localizationService;

    @Override
    public String command() {
        return "language";
    }

    @Override
    protected LocalizationService localizationService() {
        return localizationService;
    }

    @Override
    protected String mode() {
        return "l";
    }
}
