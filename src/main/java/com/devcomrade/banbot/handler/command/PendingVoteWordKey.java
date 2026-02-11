package com.devcomrade.banbot.handler.command;

import com.devcomrade.banbot.handler.VoteType;

public record PendingVoteWordKey(long chatId, VoteType voteType) {
}
