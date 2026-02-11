package com.devcomrade.banbot.handler.command.handler.fallbacks;

import com.devcomrade.banbot.components.IcuFormatters;
import com.devcomrade.banbot.handler.VoteType;
import com.devcomrade.banbot.handler.action.*;
import com.devcomrade.banbot.service.LocalizationService;
import com.devcomrade.banbot.service.RequestService;
import com.devcomrade.banbot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VoteMuteCommandHandler extends VoteCommandAbstract {
    private final LocalizationService localizationService;
    private final RequestService requestService;
    private final UserService userService;
    private final IcuFormatters pluralFormatter;

    @Override
    public String command() {
        return FallbackCommands.VOTE_MUTE.command();
    }

    @Override
    protected VoteType voteType() {
        return VoteType.MUTE;
    }

    @Override
    protected String i18nKeyPrefix() {
        return "vote_mute";
    }

    @Override
    protected LocalizationService localizationService() {
        return localizationService;
    }

    @Override
    protected RequestService requestService() {
        return requestService;
    }

    @Override
    protected UserService userService() {
        return userService;
    }

    @Override
    protected BotAction createAction(long chatId, long userId, long untilDateEpochSeconds) {
        return new MuteMemberAction(chatId, userId, untilDateEpochSeconds);
    }

    @Override
    protected IcuFormatters pluralFormatter() {
        return pluralFormatter;
    }
}
