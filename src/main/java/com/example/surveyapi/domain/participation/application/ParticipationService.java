package com.example.surveyapi.domain.participation.application;

import org.springframework.stereotype.Service;

import com.example.surveyapi.domain.participation.domain.participation.ParticipationRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ParticipationService {

	private final ParticipationRepository participationRepository;
}
