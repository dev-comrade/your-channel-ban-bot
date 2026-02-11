package com.devcomrade.banbot.handler.command.handler.fallbacks;

import com.devcomrade.banbot.components.BotIdentity;
import com.devcomrade.banbot.handler.VoteType;
import com.devcomrade.banbot.handler.command.PendingVoteWord;
import com.devcomrade.banbot.handler.command.PendingVoteWordKey;
import com.devcomrade.banbot.service.ChatService;
import com.devcomrade.banbot.service.LocalizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class SetVoteBanWordCommand extends SetVoteWordAbstract {
    private final LocalizationService localizationService;
    private final ChatService chatService;
    private final Map<PendingVoteWordKey, PendingVoteWord> pendingVoteWords;
    private final BotIdentity botIdentity;

    @Override
    public String command() {
        return FallbackCommands.SET_BAN_VOTE_WORD.command();
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
    protected LocalizationService localizationService() {
        return localizationService;
    }

    @Override
    protected void saveVoteWord(long chatId, String word) {
        chatService.setVoteBanWord(chatId, word);
    }

    @Override
    protected BotIdentity botIdentity() {
        return botIdentity;
    }

}
