package com.devcomrade.banbot.service;

import com.devcomrade.banbot.model.TelegramUser;
import org.telegram.telegrambots.meta.api.objects.User;
import com.devcomrade.banbot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public TelegramUser getOrCreateUser(User telegramUser) {
        return userRepository.findByTelegramId(telegramUser.getId())
                .orElseGet(() -> createUser(telegramUser));
    }

    private TelegramUser createUser(User telegramUser) {
        var user = new TelegramUser();
        user.setTelegramId(telegramUser.getId());
        user.setUsername(telegramUser.getUserName());
        user.setFirstName(telegramUser.getFirstName());
        user.setLastName(telegramUser.getLastName());
        user.setLanguageCode(telegramUser.getLanguageCode());
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        return userRepository.save(user);
    }

    public String getUserDisplay(User telegramUser) {
        // Приоритет отдается имени пользователя, затем имени и фамилии
        if (telegramUser.getUserName() != null && !telegramUser.getUserName().isEmpty()) {
            return "@" + telegramUser.getUserName();
        }
        StringBuilder name = new StringBuilder();
        name.append(telegramUser.getFirstName());
        if (telegramUser.getLastName() != null) {
            if (!name.isEmpty()) name.append(" ");
            name.append(telegramUser.getLastName());
        }
        return !name.isEmpty() ? name.toString() : "Пользователь";
    }
}
