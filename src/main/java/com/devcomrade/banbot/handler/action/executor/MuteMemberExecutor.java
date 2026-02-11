package com.devcomrade.banbot.handler.action.executor;

import com.devcomrade.banbot.handler.action.MuteMemberAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.groupadministration.RestrictChatMember;
import org.telegram.telegrambots.meta.api.objects.ChatPermissions;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class MuteMemberExecutor implements ActionExecutor<MuteMemberAction> {
    private final TelegramClient telegramClient;

    @Override
    public Class<MuteMemberAction> supports() {
        return MuteMemberAction.class;
    }

    @Override
    public void execute(MuteMemberAction action) throws TelegramApiException {
        var perms = ChatPermissions.builder()
                .canSendMessages(false)
                .canSendAudios(false)
                .canSendDocuments(false)
                .canSendPhotos(false)
                .canSendVideos(false)
                .canSendVideoNotes(false)
                .canSendVoiceNotes(false)
                .canSendPolls(false)
                .canSendOtherMessages(false)
                .canAddWebPagePreviews(false)
                .canChangeInfo(false)
                .canInviteUsers(false)
                .canPinMessages(false)
                .canManageTopics(false)
                .build();

        var builder = RestrictChatMember.builder()
                .chatId(action.chatId())
                .userId(action.userId())
                .permissions(perms);

        if (action.untilDateEpochSeconds() > 0) {
            builder.untilDate((int) action.untilDateEpochSeconds());
        }

        telegramClient.execute(builder.build());
    }
}
