package com.devcomrade.banbot.handler.callback;

import jakarta.annotation.Nonnull;

import java.util.List;

public record CallbackData(String raw, String kind, List<String> parts) {
    public static CallbackData parse(@Nonnull String raw) {
        var p = raw.split("\\|");
        return new CallbackData(raw, p[0], List.of(p).subList(1, p.length));
    }
}
