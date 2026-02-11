package com.devcomrade.banbot.handler.context;

import com.devcomrade.banbot.handler.callback.CallbackData;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.InaccessibleMessage;
import org.telegram.telegrambots.meta.api.objects.message.MaybeInaccessibleMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.util.Optional;

public record CallbackContext(
        CallbackData callbackData,
        UpdateContext updateContext,
        User voter,
        MaybeInaccessibleMessage mi
) {
    public int messageId() {
        return switch (mi) {
            case Message m -> m.getMessageId();
            case InaccessibleMessage im -> im.getMessageId();
            default -> throw new IllegalStateException("Unsupported message type: " + mi);
        };
    }

    public Optional<Message> message() {
        return (mi instanceof Message m) ? Optional.of(m) : Optional.empty();
    }
}