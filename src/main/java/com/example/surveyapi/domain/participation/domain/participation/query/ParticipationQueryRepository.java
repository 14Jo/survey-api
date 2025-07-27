package com.example.surveyapi.domain.participation.domain.participation.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ParticipationQueryRepository {

	Page<ParticipationInfo> findParticipationsInfo(Long memberId, Pageable pageable);
}
