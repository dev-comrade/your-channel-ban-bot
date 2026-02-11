package com.devcomrade.banbot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;


@Service
@RequiredArgsConstructor
public class LocalizationService {
    private final MessageSource messageSource;

    public String getMessage(String language, String key, Object... args) {
        return messageSource.getMessage(key, args, toLocale(language));
    }

    private Locale toLocale(String language) {
        if (language == null || language.isBlank()) {
            return Locale.ENGLISH;
        }
        var locale = Locale.forLanguageTag(language);
        return locale.getLanguage().isBlank() ? Locale.ENGLISH : locale;
    }
}
