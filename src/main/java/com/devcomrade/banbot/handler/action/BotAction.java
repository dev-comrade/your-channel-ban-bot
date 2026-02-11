package com.devcomrade.banbot.handler.action;

public sealed interface BotAction permits SendTextAction, EditTextAction, BanMemberAction, DeleteMessageAction, MuteMemberAction {
}
