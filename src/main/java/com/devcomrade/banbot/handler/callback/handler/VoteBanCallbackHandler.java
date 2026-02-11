package com.devcomrade.banbot.handler.callback.handler;

import com.devcomrade.banbot.components.IcuFormatters;
import com.devcomrade.banbot.handler.VoteType;
import com.devcomrade.banbot.handler.action.BanMemberAction;
import com.devcomrade.banbot.handler.action.BotAction;
import com.devcomrade.banbot.handler.command.handler.fallbacks.FallbackCommands;
import com.devcomrade.banbot.service.LocalizationService;
import com.devcomrade.banbot.service.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VoteBanCallbackHandler extends VoteCallbackAbstract {
    private final LocalizationService localizationService;
    private final RequestService requestService;
    private final IcuFormatters pluralFormatter;

    @Override
    public String getKind() {
        return FallbackCommands.VOTE_BAN.command();
    }

    @Override
    public RequestService requestService() {
        return requestService;
    }

    @Override
    public VoteType voteType() {
        return VoteType.BAN;
    }

    @Override
    public LocalizationService localizationService() {
        return localizationService;
    }

    @Override
    public BotAction pickerAction(Long chatId, Long userId, long untilDateEpochSeconds) {
        return new BanMemberAction(chatId, userId, untilDateEpochSeconds);
    }

    @Override
    String i18nKeyPrefix() {
        return "vote_ban";
    }

    @Override
    IcuFormatters pluralFormatter() {
        return pluralFormatter;
    }
}
