package com.devcomrade.banbot.handler.command.handler.fallbacks;

public enum FallbackCommands {
    VOTE_BAN("vote_ban"),
    VOTE_MUTE("vote_mute"),
    SET_BAN_VOTE_WORD("set_ban_vote_word"),
    SET_MUTE_VOTE_WORD("set_mute_vote_word")
    ;

    private final String command;

    FallbackCommands(String command) {
        this.command = command;
    }

    public String command() {
        return command;
    }
}
