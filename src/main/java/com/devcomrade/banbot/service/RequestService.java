package com.devcomrade.banbot.service;

import com.devcomrade.banbot.handler.VoteType;
import com.devcomrade.banbot.model.Request;
import com.devcomrade.banbot.repository.RequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RequestService {
    private final RequestRepository requestRepository;

    public Request createRequest(Long chatId, User candidate, Long messageId, boolean silent, VoteType voteType) {
        Request request = new Request();
        request.setChatId(chatId);
        request.setCandidateId(candidate.getId());
        request.setCandidateUsername(candidate.getUserName());
        request.setCandidateFirstName(candidate.getFirstName());
        request.setCandidateLastName(candidate.getLastName());
        request.setMessageId(messageId);
        request.setSilent(silent);
        request.setCreatedAt(Instant.now());
        request.setUpdatedAt(Instant.now());
        return requestRepository.save(request);
    }

    public Optional<Request> findRequest(Long chatId, Long candidateId, VoteType voteType) {
        return requestRepository.findByChatIdAndCandidateId(chatId, candidateId, voteType);
    }

    public Request addVote(Request request, Long voterId) {
        if (!request.getVoters().contains(voterId)) {
            request.getVoters().add(voterId);
            request.setVotesCount(request.getVoters().size());
            request.setUpdatedAt(Instant.now());
            return requestRepository.save(request);
        }
        return request;
    }

    public void deleteRequest(Long chatId, Long candidateId, VoteType voteType) {
        requestRepository.deleteByChatIdAndCandidateId(chatId, candidateId, voteType);
    }

    public String getCandidateDisplay(Request request) {
        if (request.getCandidateUsername() != null && !request.getCandidateUsername().isEmpty()) {
            return "@" + request.getCandidateUsername();
        }
        var name = new StringBuilder();
        if (request.getCandidateFirstName() != null) {
            name.append(request.getCandidateFirstName());
        }
        if (request.getCandidateLastName() != null) {
            if (!name.isEmpty()) name.append(" ");
            name.append(request.getCandidateLastName());
        }
        return !name.isEmpty() ? name.toString() : "User";
    }
}
