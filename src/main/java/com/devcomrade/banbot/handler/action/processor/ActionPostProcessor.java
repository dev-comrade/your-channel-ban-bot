package com.devcomrade.banbot.handler.action.processor;

import com.devcomrade.banbot.handler.action.BotAction;

import java.util.List;

public interface ActionPostProcessor {
    List<BotAction> process(List<BotAction> actions);
}