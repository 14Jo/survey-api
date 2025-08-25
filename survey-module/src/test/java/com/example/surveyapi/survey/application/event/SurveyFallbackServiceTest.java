package com.example.surveyapi.survey.application.event;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.surveyapi.survey.domain.survey.Survey;
import com.example.surveyapi.survey.domain.survey.SurveyRepository;
import com.example.surveyapi.survey.domain.survey.enums.SurveyStatus;
import com.example.surveyapi.survey.domain.survey.vo.SurveyDuration;
import com.example.surveyapi.global.event.RabbitConst;
import com.example.surveyapi.global.event.survey.SurveyStartDueEvent;
import com.example.surveyapi.global.event.survey.SurveyEndDueEvent;

@ExtendWith(MockitoExtension.class)
class SurveyFallbackServiceTest {

	@Mock
	private SurveyRepository surveyRepository;

	@InjectMocks
	private SurveyFallbackService surveyFallbackService;

	@Test
	@DisplayName("과거 시간의 설문 시작 이벤트 실패 시 즉시 처리")
	void testHandleFailedSurveyStartEventPastTime() {
		// Given
		LocalDateTime pastTime = LocalDateTime.now().minusHours(1);
		SurveyStartDueEvent event = new SurveyStartDueEvent(1L, 1L, pastTime);
		
		Survey mockSurvey = mock(Survey.class);
		when(surveyRepository.findById(1L)).thenReturn(Optional.of(mockSurvey));
		when(mockSurvey.getStatus()).thenReturn(SurveyStatus.PREPARING);
		when(mockSurvey.getDuration()).thenReturn(mock(SurveyDuration.class));

		// When
		surveyFallbackService.handleFailedEvent(event, RabbitConst.ROUTING_KEY_SURVEY_START_DUE, "Connection failed");

		// Then
		verify(mockSurvey).applyDurationChange(any(), any());
		verify(surveyRepository).save(mockSurvey);
	}

	@Test
	@DisplayName("미래 시간의 설문 종료 이벤트 실패 시 로그만 기록")
	void testHandleFailedSurveyEndEventFutureTime() {
		// Given
		LocalDateTime futureTime = LocalDateTime.now().plusHours(1);
		SurveyEndDueEvent event = new SurveyEndDueEvent(1L, 1L, futureTime);
		
		Survey mockSurvey = mock(Survey.class);
		when(surveyRepository.findById(1L)).thenReturn(Optional.of(mockSurvey));
		when(mockSurvey.getStatus()).thenReturn(SurveyStatus.IN_PROGRESS);

		// When
		surveyFallbackService.handleFailedEvent(event, RabbitConst.ROUTING_KEY_SURVEY_END_DUE, "Connection failed");

		// Then
		verify(mockSurvey, never()).applyDurationChange(any(), any());
		verify(surveyRepository, never()).save(mockSurvey);
	}

	@Test
	@DisplayName("존재하지 않는 설문 ID에 대한 처리")
	void testHandleFailedEventWithNonExistentSurvey() {
		// Given
		LocalDateTime pastTime = LocalDateTime.now().minusHours(1);
		SurveyStartDueEvent event = new SurveyStartDueEvent(999L, 1L, pastTime);
		
		when(surveyRepository.findById(999L)).thenReturn(Optional.empty());

		// When
		surveyFallbackService.handleFailedEvent(event, RabbitConst.ROUTING_KEY_SURVEY_START_DUE, "Connection failed");

		// Then
		verify(surveyRepository, never()).save(any());
	}
}