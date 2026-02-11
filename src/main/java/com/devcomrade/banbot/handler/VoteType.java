package com.devcomrade.banbot.handler;

import com.fasterxml.jackson.annotation.JsonAlias;

public enum VoteType {
    @JsonAlias("mute")
    MUTE,
    @JsonAlias("ban")
    BAN
}
