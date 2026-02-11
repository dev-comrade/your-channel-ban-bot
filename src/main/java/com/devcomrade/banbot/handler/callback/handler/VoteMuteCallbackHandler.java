package com.devcomrade.banbot.handler.callback.handler;

import com.devcomrade.banbot.components.IcuFormatters;
import com.devcomrade.banbot.handler.VoteType;
import com.devcomrade.banbot.handler.action.BotAction;
import com.devcomrade.banbot.handler.action.MuteMemberAction;
import com.devcomrade.banbot.handler.command.handler.fallbacks.FallbackCommands;
import com.devcomrade.banbot.service.LocalizationService;
import com.devcomrade.banbot.service.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VoteMuteCallbackHandler extends VoteCallbackAbstract {
    private final LocalizationService localizationService;
    private final RequestService requestService;
    private final IcuFormatters pluralFormatter;

    @Override
    public String getKind() {
        return FallbackCommands.VOTE_MUTE.command();
    }

    @Override
    public RequestService requestService() {
        return requestService;
    }

    @Override
    public VoteType voteType() {
        return VoteType.MUTE;
    }

    @Override
    public LocalizationService localizationService() {
        return localizationService;
    }

    @Override
    public BotAction pickerAction(Long chatId, Long userId, long untilDateEpochSeconds) {
        return new MuteMemberAction(chatId, userId, untilDateEpochSeconds);
    }

    @Override
    String i18nKeyPrefix() {
        return "vote_mute";
    }

    @Override
    IcuFormatters pluralFormatter() {
        return pluralFormatter;
    }
}
