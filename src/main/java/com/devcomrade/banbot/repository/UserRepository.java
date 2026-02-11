package com.devcomrade.banbot.repository;

import com.devcomrade.banbot.model.TelegramUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<TelegramUser, String> {
    Optional<TelegramUser> findByTelegramId(Long telegramId);
}
