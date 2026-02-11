package com.devcomrade.banbot.handler.action.executor;

import com.devcomrade.banbot.handler.action.BanMemberAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.groupadministration.BanChatMember;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class BanMemberExecutor implements ActionExecutor<BanMemberAction> {
    private final TelegramClient telegramClient;

    @Override
    public Class<BanMemberAction> supports() {
        return BanMemberAction.class;
    }

    @Override
    public void execute(BanMemberAction action) throws TelegramApiException {
        var builder = BanChatMember.builder()
                .chatId(action.chatId())
                .userId(action.userId())
                .revokeMessages(true);

        if (action.untilDateEpochSeconds() > 0) {
            builder.untilDate((int) action.untilDateEpochSeconds());
        }

        telegramClient.execute(builder.build());
    }
}
