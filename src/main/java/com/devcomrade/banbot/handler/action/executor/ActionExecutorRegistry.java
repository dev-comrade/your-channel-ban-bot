package com.devcomrade.banbot.handler.action.executor;

import com.devcomrade.banbot.handler.action.BotAction;
import com.devcomrade.banbot.handler.action.processor.ActionPostProcessor;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ActionExecutorRegistry {
    private final List<ActionExecutor<?>> executors;
    private final Map<Class<?>, ActionExecutor<?>> index = new HashMap<>();
    private final List<ActionPostProcessor> postProcessors;

    @PostConstruct
    void init() {
        for (var executor : executors) {
            index.put(executor.supports(), executor);
        }
        log.info("Registered {} action executors", index.size());
    }

    @SuppressWarnings("unchecked")
    public void execute(BotAction action) throws TelegramApiException {
        var executor = index.get(action.getClass());
        if (executor == null) {
            log.warn("No ActionExecutor found for {}", action.getClass().getName());
            return;
        }
        ((ActionExecutor<BotAction>) executor).execute(action);
    }

    public void executeList(List<BotAction> actions) {
        var processed = actions;
        for (var p : postProcessors) {
            processed = p.process(processed);
        }

        for (var action : processed) {
            try {
                execute(action);
            } catch (TelegramApiException e) {
                log.error("Can't execute action {}", action.getClass().getName(), e);
            }
        }

    }
}