package com.devcomrade.banbot.service;

import com.devcomrade.banbot.model.Chat;
import com.devcomrade.banbot.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;

    public Chat getOrCreateChat(Long chatId) {
        return chatRepository.findByChatId(chatId)
                .orElseGet(() -> createChat(chatId));
    }

    public Chat getChat(Long chatId) {
        return chatRepository.findByChatId(chatId)
                .orElseThrow();
    }

    private Chat createChat(Long chatId) {
        var chat = new Chat();
        chat.setChatId(chatId);
        chat.setCreatedAt(Instant.now());
        chat.setUpdatedAt(Instant.now());
        return chatRepository.save(chat);
    }

    public Chat updateChat(Chat chat) {
        chat.setUpdatedAt(Instant.now());
        return chatRepository.save(chat);
    }

    public Chat setLanguage(Long chatId, String language) {
        var chat = getOrCreateChat(chatId);
        chat.setLanguage(language);
        return updateChat(chat);
    }

    public Chat setLimit(Long chatId, Integer limit) {
        var chat = getOrCreateChat(chatId);
        chat.setVoteLimit(limit);
        return updateChat(chat);
    }

    public Chat setMuteTime(Long chatId, Integer time) {
        var chat = getOrCreateChat(chatId);
        chat.setMuteTime(time);
        return updateChat(chat);
    }

    public Chat setLocked(Long chatId, Boolean locked) {
        var chat = getOrCreateChat(chatId);
        chat.setLocked(locked);
        return updateChat(chat);
    }

    public Chat setVoteBanWord(Long chatId, String word) {
        var chat = getOrCreateChat(chatId);
        chat.setVoteBanWord(word);
        return updateChat(chat);
    }

    public Chat setVoteMuteWord(Long chatId, String word) {
        var chat = getOrCreateChat(chatId);
        chat.setVoteMuteWord(word);
        return updateChat(chat);
    }
}
