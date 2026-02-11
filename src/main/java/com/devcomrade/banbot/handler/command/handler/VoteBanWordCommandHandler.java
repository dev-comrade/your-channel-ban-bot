package com.devcomrade.banbot.handler.command.handler;

import com.devcomrade.banbot.handler.VoteType;
import com.devcomrade.banbot.handler.command.PendingVoteWord;
import com.devcomrade.banbot.handler.command.PendingVoteWordKey;
import com.devcomrade.banbot.model.Chat;
import com.devcomrade.banbot.service.LocalizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
@RequiredArgsConstructor
public class VoteBanWordCommandHandler extends VoteWordCommandAbstract {
    private final LocalizationService localizationService;
    private final Map<PendingVoteWordKey, PendingVoteWord> pendingVoteWords;

    @Override
    public String command() {
        return "vote_ban_word";
    }

    @Override
    protected LocalizationService localizationService() {
        return localizationService;
    }

    @Override
    protected VoteType voteType() {
        return VoteType.BAN;
    }

    @Override
    protected Map<PendingVoteWordKey, PendingVoteWord> pendingVoteWords() {
        return pendingVoteWords;
    }

    @Override
    protected String currentCharWord(Chat chat) {
        return chat.getVoteBanWord();
    }
}
