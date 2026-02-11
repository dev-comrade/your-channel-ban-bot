package com.devcomrade.banbot.handler;

import com.devcomrade.banbot.config.TelegramBotProperties;
import com.devcomrade.banbot.handler.action.executor.ActionExecutorRegistry;
import com.devcomrade.banbot.handler.callback.CallbackFacade;
import com.devcomrade.banbot.handler.command.CommandFacade;
import com.devcomrade.banbot.handler.context.UpdateContextResolver;
import com.devcomrade.banbot.service.LocalizationService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeAllChatAdministrators;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeAllGroupChats;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeAllPrivateChats;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChannelGuardBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;
    private final CallbackFacade callbackFacade;
    private final CommandFacade commandFacade;
    private final LocalizationService localizationService;
    private final ActionExecutorRegistry actionExecutorRegistry;
    private final TelegramBotProperties botProps;
    private final UpdateContextResolver updateContextResolver;

    @PostConstruct
    public void init() {
        setupBotCommands();
    }

    @Override
    public String getBotToken() {
        return botProps.getToken();
    }

    @Override
    public LongPollingSingleThreadUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        log.info("Received update: {}", update);

        try {
            var updateContext = updateContextResolver.resolve(update);
            if (update.hasCallbackQuery()) {
                var cb = update.getCallbackQuery();
                actionExecutorRegistry.executeList(
                        callbackFacade.handleCallbackQuery(cb, updateContext)
                );
            } else if (update.hasMessage()) {
                var msg = update.getMessage();
                actionExecutorRegistry.executeList(
                        commandFacade.handleCommand(msg, updateContext)
                );
            }
        } catch (Exception e) {
            log.error("Error processing update", e);
        }
    }

    private void setupBotCommands() {
        try {
            setupCommandsForLanguage("ru");
            setupCommandsForLanguage("en");
        } catch (TelegramApiException e) {
            log.error("Failed to set up bot commands", e);
        }
    }

    private void setupCommandsForLanguage(String languageCode) throws TelegramApiException {
        telegramClient.execute(SetMyCommands.builder()
                .commands(List.of(new BotCommand("/help", localizationService.getMessage(languageCode, "commands.help"))))
                .scope(new BotCommandScopeAllPrivateChats())
                .languageCode(languageCode)
                .build());

        telegramClient.execute(SetMyCommands.builder()
                .commands(List.of(new BotCommand("/help", localizationService.getMessage(languageCode, "commands.help"))))
                .scope(new BotCommandScopeAllGroupChats())
                .languageCode(languageCode)
                .build());

        telegramClient.execute(SetMyCommands.builder()
                .commands(List.of(new BotCommand("/help", localizationService.getMessage(languageCode, "commands.help"))))
                .scope(new BotCommandScopeAllChatAdministrators())
                .languageCode(languageCode)
                .build());
    }
}