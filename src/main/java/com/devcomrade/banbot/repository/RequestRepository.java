package com.devcomrade.banbot.repository;

import com.devcomrade.banbot.handler.VoteType;
import com.devcomrade.banbot.model.Request;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RequestRepository extends MongoRepository<Request, String> {
    Optional<Request> findByChatIdAndCandidateId(Long chatId, Long candidateId, VoteType voteType);
    void deleteByChatIdAndCandidateId(Long chatId, Long candidateId, VoteType voteType);
}
