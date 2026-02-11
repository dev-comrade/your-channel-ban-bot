package com.devcomrade.banbot.handler.action.executor;

import com.devcomrade.banbot.handler.action.BotAction;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface ActionExecutor<T extends BotAction> {

    /**
     * Type action, which executor supports.
     */
    Class<T> supports();

    /**
     * Execute action.
     */
    void execute(T action) throws TelegramApiException;
}
