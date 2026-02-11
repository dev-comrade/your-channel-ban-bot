package com.devcomrade.banbot.config;

import com.devcomrade.banbot.handler.command.PendingVoteWord;
import com.devcomrade.banbot.handler.command.PendingVoteWordKey;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class CacheConfig {
    @Value("${cache.chat-admins.ttl-minutes:5}")
    private int cacheTtlMinutes;

    private final Map<PendingVoteWordKey, PendingVoteWord> pendingVoteWords = new ConcurrentHashMap<>();

    @Bean
    public Map<PendingVoteWordKey, PendingVoteWord> getPendingVoteWords() {
        return pendingVoteWords;
    }

    @Bean
    public CacheManager cacheManager() {
        var caffeine = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(cacheTtlMinutes))
                .maximumSize(10_000);

        var manager = new CaffeineCacheManager("chatAdmins");
        manager.setCaffeine(caffeine);
        return manager;
    }
}
