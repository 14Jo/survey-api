package com.example.surveyapi.domain.participation.domain.participation.query;

import com.example.surveyapi.domain.participation.domain.command.ResponseData;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ParticipationProjection {
    private final Long surveyId;
    private final Long participationId;
    private final LocalDateTime participatedAt;
    private final List<ResponseData> responses;

    public ParticipationProjection(Long surveyId, Long participationId, LocalDateTime participatedAt, List<ResponseData> responses) {
        this.surveyId = surveyId;
        this.participationId = participationId;
        this.participatedAt = participatedAt;
        this.responses = responses;
    }
}
