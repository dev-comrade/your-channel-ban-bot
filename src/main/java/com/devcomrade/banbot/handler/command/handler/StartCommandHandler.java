package com.devcomrade.banbot.handler.command.handler;

import com.devcomrade.banbot.service.LocalizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class StartCommandHandler extends LanguageCommandAbstract {
    private final LocalizationService localizationService;

    @Override
    public String command() {
        return "start";
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
