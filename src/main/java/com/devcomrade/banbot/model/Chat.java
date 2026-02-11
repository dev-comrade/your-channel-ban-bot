package com.devcomrade.banbot.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document(collection = "chats")
public class Chat {
    @Id
    private String id;
    
    private Long chatId;
    private String language = "en";
    private Integer voteLimit = 5;
    private Integer muteTime = 24 * 60 * 60; // секунды (по умолчанию 24 часа)
    private Boolean locked = false;
    private String voteBanWord = "kick";
    private String voteMuteWord = "mute";
    private boolean isBotAdmin = false;
    private boolean isUserChat = false;

    private Instant createdAt;
    private Instant updatedAt;
}
