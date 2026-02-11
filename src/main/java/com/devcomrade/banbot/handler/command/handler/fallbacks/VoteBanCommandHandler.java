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
public class VoteBanCommandHandler extends VoteCommandAbstract {
    private final LocalizationService localizationService;
    private final RequestService requestService;
    private final UserService userService;
    private final IcuFormatters pluralFormatter;

    @Override
    public String command() {
        return FallbackCommands.VOTE_BAN.command();
    }

    @Override
    protected VoteType voteType() {
        return VoteType.BAN;
    }

    @Override
    protected String i18nKeyPrefix() {
        return "vote_ban";
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
        return new BanMemberAction(chatId, userId, untilDateEpochSeconds);
    }

    @Override
    protected IcuFormatters pluralFormatter() {
        return pluralFormatter;
    }
}
