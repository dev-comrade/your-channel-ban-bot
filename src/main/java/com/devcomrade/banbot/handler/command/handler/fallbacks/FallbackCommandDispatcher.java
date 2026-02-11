package com.devcomrade.banbot.handler.command.handler.fallbacks;

import com.devcomrade.banbot.handler.VoteType;
import com.devcomrade.banbot.handler.command.PendingVoteWord;
import com.devcomrade.banbot.handler.command.PendingVoteWordKey;
import com.devcomrade.banbot.model.Chat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class FallbackCommandDispatcher {
    private final Map<PendingVoteWordKey, PendingVoteWord> pendingVoteWords;

    public String dispatch(Message message, Chat chat) {
        if (message == null || message.getText() == null) return null;

        if (pendingVoteWords.containsKey(new PendingVoteWordKey(message.getChatId(), VoteType.BAN))) {
            return FallbackCommands.SET_BAN_VOTE_WORD.command();
        } else if (pendingVoteWords.containsKey(new PendingVoteWordKey(message.getChatId(), VoteType.MUTE))) {
            return FallbackCommands.SET_MUTE_VOTE_WORD.command();
        } else if (message.getText().toLowerCase().contains(chat.getVoteBanWord().toLowerCase())) {
            return FallbackCommands.VOTE_BAN.command();
        } else if (message.getText().toLowerCase().contains(chat.getVoteMuteWord().toLowerCase())) {
            return FallbackCommands.VOTE_MUTE.command();
        }

        return null;
    }
}
