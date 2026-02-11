package com.devcomrade.banbot.model;

import com.devcomrade.banbot.handler.VoteType;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "requests")
public class Request {
    @Id
    private String id;
    
    private Long chatId;
    private Long candidateId;
    private String candidateUsername;
    private String candidateFirstName;
    private String candidateLastName;
    private VoteType voteType;

    private Integer votesCount = 0;
    private List<Long> voters = new ArrayList<>();
    
    private Long messageId;
    private Boolean silent = false;
    
    private Instant createdAt;
    private Instant updatedAt;
}
