package com.example.surveyapi.domain.participation.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.surveyapi.domain.participation.application.client.SurveyDetailDto;
import com.example.surveyapi.domain.participation.application.client.SurveyInfoDto;
import com.example.surveyapi.domain.participation.application.client.SurveyServicePort;
import com.example.surveyapi.domain.participation.application.client.UserServicePort;
import com.example.surveyapi.domain.participation.application.client.UserSnapshotDto;
import com.example.surveyapi.domain.participation.application.client.enums.SurveyApiQuestionType;
import com.example.surveyapi.domain.participation.application.client.enums.SurveyApiStatus;
import com.example.surveyapi.domain.participation.application.dto.request.CreateParticipationRequest;
import com.example.surveyapi.domain.participation.application.dto.response.ParticipationDetailResponse;
import com.example.surveyapi.domain.participation.application.dto.response.ParticipationGroupResponse;
import com.example.surveyapi.domain.participation.application.dto.response.ParticipationInfoResponse;
import com.example.surveyapi.domain.participation.domain.command.ResponseData;
import com.example.surveyapi.domain.participation.domain.participation.Participation;
import com.example.surveyapi.domain.participation.domain.participation.ParticipationRepository;
import com.example.surveyapi.domain.participation.domain.participation.enums.Gender;
import com.example.surveyapi.domain.participation.domain.participation.query.ParticipationInfo;
import com.example.surveyapi.domain.participation.domain.participation.vo.ParticipantInfo;
import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyStatus;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

@ExtendWith(MockitoExtension.class)
class ParticipationServiceTest {

	@InjectMocks
	private ParticipationService participationService;

	@Mock
	private ParticipationRepository participationRepository;

	@Mock
	private SurveyServicePort surveyServicePort;

	@Mock
	private UserServicePort userServicePort;

	private Long surveyId;
	private Long userId;
	private String authHeader;
	private CreateParticipationRequest request;
	private SurveyDetailDto surveyDetailDto;
	private UserSnapshotDto userSnapshotDto;

	@BeforeEach
	void setUp() {
		surveyId = 1L;
		userId = 1L;
		authHeader = "Bearer token";

		List<ResponseData> responseDataList = List.of(
			createResponseData(1L, Map.of("textAnswer", "주관식 및 서술형")),
			createResponseData(2L, Map.of("choices", List.of(1, 3)))
		);
		request = createParticipationRequest(responseDataList);

		surveyDetailDto = new SurveyDetailDto();
		ReflectionTestUtils.setField(surveyDetailDto, "surveyId", surveyId);
		ReflectionTestUtils.setField(surveyDetailDto, "status", SurveyApiStatus.IN_PROGRESS);
		SurveyDetailDto.Duration duration = new SurveyDetailDto.Duration();
		ReflectionTestUtils.setField(duration, "endDate", LocalDateTime.now().plusDays(1));
		ReflectionTestUtils.setField(surveyDetailDto, "duration", duration);
		SurveyDetailDto.Option option = new SurveyDetailDto.Option();
		ReflectionTestUtils.setField(option, "allowResponseUpdate", true);
		ReflectionTestUtils.setField(surveyDetailDto, "option", option);
		List<SurveyDetailDto.QuestionValidationInfo> questions = List.of(
			createQuestionValidationInfo(1L, false, SurveyApiQuestionType.SHORT_ANSWER),
			createQuestionValidationInfo(2L, true, SurveyApiQuestionType.MULTIPLE_CHOICE)
		);
		ReflectionTestUtils.setField(surveyDetailDto, "questions", questions);

		userSnapshotDto = new UserSnapshotDto();
		ReflectionTestUtils.setField(userSnapshotDto, "birth", "2000-01-01T00:00:00");
		ReflectionTestUtils.setField(userSnapshotDto, "gender", Gender.MALE);
		UserSnapshotDto.Region region = new UserSnapshotDto.Region();
		ReflectionTestUtils.setField(region, "province", "서울");
		ReflectionTestUtils.setField(region, "district", "강남구");
		ReflectionTestUtils.setField(userSnapshotDto, "region", region);
	}

	private ResponseData createResponseData(Long questionId, Map<String, Object> answer) {
		ResponseData responseData = new ResponseData();
		ReflectionTestUtils.setField(responseData, "questionId", questionId);
		ReflectionTestUtils.setField(responseData, "answer", answer);
		return responseData;
	}

	private CreateParticipationRequest createParticipationRequest(List<ResponseData> responseDataList) {
		CreateParticipationRequest request = new CreateParticipationRequest();
		ReflectionTestUtils.setField(request, "responseDataList", responseDataList);
		return request;
	}

	private SurveyDetailDto.QuestionValidationInfo createQuestionValidationInfo(Long questionId, boolean isRequired,
		SurveyApiQuestionType type) {
		SurveyDetailDto.QuestionValidationInfo question = new SurveyDetailDto.QuestionValidationInfo();
		ReflectionTestUtils.setField(question, "questionId", questionId);
		ReflectionTestUtils.setField(question, "isRequired", isRequired);
		ReflectionTestUtils.setField(question, "questionType", type);
		return question;
	}

	@Test
	@DisplayName("설문 응답 제출")
	void createParticipation() {
		// given
		given(participationRepository.exists(surveyId, userId)).willReturn(false);
		given(surveyServicePort.getSurveyDetail(authHeader, surveyId)).willReturn(surveyDetailDto);
		given(userServicePort.getParticipantInfo(authHeader, userId)).willReturn(userSnapshotDto);

		Participation savedParticipation = Participation.create(userId, surveyId,
			ParticipantInfo.of("2000-01-01T00:00:00", Gender.MALE, "서울", "강남구"),
			request.getResponseDataList());
		ReflectionTestUtils.setField(savedParticipation, "id", 1L);
		given(participationRepository.save(any(Participation.class))).willReturn(savedParticipation);

		// when
		Long participationId = participationService.create(authHeader, surveyId, userId, request);

		// then
		assertThat(participationId).isEqualTo(1L);
		then(participationRepository).should().save(any(Participation.class));
	}

	@Test
	@DisplayName("설문 응답 제출 실패 - 이미 참여한 설문")
	void createParticipation_alreadyParticipated() {
		// given
		given(participationRepository.exists(surveyId, userId)).willReturn(true);

		// when & then
		assertThatThrownBy(() -> participationService.create(authHeader, surveyId, userId, request))
			.isInstanceOf(CustomException.class)
			.hasMessage(CustomErrorCode.SURVEY_ALREADY_PARTICIPATED.getMessage());
	}

	@Test
	@DisplayName("설문 응답 제출 실패 - 설문이 진행중이 아님")
	void createParticipation_surveyNotActive() {
		// given
		ReflectionTestUtils.setField(surveyDetailDto, "status", SurveyApiStatus.CLOSED);
		given(participationRepository.exists(surveyId, userId)).willReturn(false);
		given(surveyServicePort.getSurveyDetail(authHeader, surveyId)).willReturn(surveyDetailDto);

		// when & then
		assertThatThrownBy(() -> participationService.create(authHeader, surveyId, userId, request))
			.isInstanceOf(CustomException.class)
			.hasMessage(CustomErrorCode.SURVEY_NOT_ACTIVE.getMessage());
	}

	@Test
	@DisplayName("나의 전체 참여 목록 조회")
	void getAllMyParticipation() {
		// given
		Long myUserId = 1L;
		Pageable pageable = PageRequest.of(0, 5);

		List<ParticipationInfo> participationInfos = List.of(
			new ParticipationInfo(1L, 1L, LocalDateTime.now()),
			new ParticipationInfo(2L, 3L, LocalDateTime.now())
		);
		Page<ParticipationInfo> page = new PageImpl<>(participationInfos, pageable, 2);
		given(participationRepository.findParticipationInfos(myUserId, pageable)).willReturn(page);

		List<Long> surveyIds = List.of(1L, 3L);
		List<SurveyInfoDto> surveyInfoDtos = List.of(
			createSurveyInfoDto(1L, "설문1"),
			createSurveyInfoDto(3L, "설문3")
		);
		given(surveyServicePort.getSurveyInfoList(authHeader, surveyIds)).willReturn(surveyInfoDtos);

		// when
		Page<ParticipationInfoResponse> result = participationService.gets(authHeader, myUserId, pageable);

		// then
		assertThat(result.getTotalElements()).isEqualTo(2);
		assertThat(result.getContent()).hasSize(2);
		assertThat(result.getContent().get(0).getSurveyInfo().getTitle()).isEqualTo("설문1");
		assertThat(result.getContent().get(1).getSurveyInfo().getTitle()).isEqualTo("설문3");
	}

	private SurveyInfoDto createSurveyInfoDto(Long id, String title) {
		SurveyInfoDto dto = new SurveyInfoDto();
		ReflectionTestUtils.setField(dto, "surveyId", id);
		ReflectionTestUtils.setField(dto, "title", title);
		ReflectionTestUtils.setField(dto, "status", SurveyStatus.IN_PROGRESS);
		SurveyInfoDto.Duration duration = new SurveyInfoDto.Duration();
		ReflectionTestUtils.setField(duration, "endDate", LocalDateTime.now().plusDays(1));
		ReflectionTestUtils.setField(dto, "duration", duration);
		SurveyInfoDto.Option option = new SurveyInfoDto.Option();
		ReflectionTestUtils.setField(option, "allowResponseUpdate", true);
		ReflectionTestUtils.setField(dto, "option", option);
		return dto;
	}

	@Test
	@DisplayName("나의 참여 응답 상세 조회")
	void getParticipation() {
		// given
		Long participationId = 1L;
		Participation participation = Participation.create(userId, surveyId,
			ParticipantInfo.of("2000-01-01T00:00:00", Gender.MALE, "서울", "강남구"),
			List.of(createResponseData(1L, Map.of("textAnswer", "상세 조회 답변"))));
		given(participationRepository.findById(participationId)).willReturn(Optional.of(participation));

		// when
		ParticipationDetailResponse result = participationService.get(userId, participationId);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getResponses()).hasSize(1);
		assertThat(result.getResponses().get(0).getQuestionId()).isEqualTo(1L);
		assertThat(result.getResponses().get(0).getAnswer()).isEqualTo(Map.of("textAnswer", "상세 조회 답변"));
	}

	@Test
	@DisplayName("참여 응답 수정")
	void updateParticipation() {
		// given
		Long participationId = 1L;
		List<ResponseData> updatedResponseDataList = List.of(
			createResponseData(1L, Map.of("textAnswer", "수정된 답변")),
			createResponseData(2L, Map.of("choices", List.of(2)))
		);
		CreateParticipationRequest updateRequest = createParticipationRequest(updatedResponseDataList);

		Participation participation = Participation.create(userId, surveyId,
			ParticipantInfo.of("2000-01-01T00:00:00", Gender.MALE, "서울", "강남구"),
			request.getResponseDataList());
		given(participationRepository.findById(participationId)).willReturn(Optional.of(participation));
		given(surveyServicePort.getSurveyDetail(authHeader, surveyId)).willReturn(surveyDetailDto);

		// when
		participationService.update(authHeader, userId, participationId, updateRequest);

		// then
		assertThat(participation.getResponses()).hasSize(2);
		assertThat(participation.getResponses().get(0).getAnswer()).isEqualTo(Map.of("textAnswer", "수정된 답변"));
	}

	@Test
	@DisplayName("참여 응답 수정 실패 - 수정 불가 설문")
	void updateParticipation_cannotUpdate() {
		// given
		Long participationId = 1L;
		ReflectionTestUtils.setField(surveyDetailDto.getOption(), "allowResponseUpdate", false);
		Participation participation = Participation.create(userId, surveyId,
			ParticipantInfo.of("2000-01-01T00:00:00", Gender.MALE, "서울", "강남구"),
			request.getResponseDataList());

		given(participationRepository.findById(participationId)).willReturn(Optional.of(participation));
		given(surveyServicePort.getSurveyDetail(authHeader, surveyId)).willReturn(surveyDetailDto);

		// when & then
		assertThatThrownBy(() -> participationService.update(authHeader, userId, participationId, request))
			.isInstanceOf(CustomException.class)
			.hasMessage(CustomErrorCode.CANNOT_UPDATE_RESPONSE.getMessage());
	}

	@Test
	@DisplayName("여러 설문에 대한 모든 참여 응답 기록 조회")
	void getAllBySurveyIds() {
		// given
		Long surveyId1 = 10L;
		Long surveyId2 = 20L;
		List<Long> surveyIds = List.of(surveyId1, surveyId2);

		Participation p1 = Participation.create(1L, surveyId1, mock(ParticipantInfo.class),
			List.of(createResponseData(1L, Map.of("textAnswer", "답변1-1"))));
		Participation p2 = Participation.create(2L, surveyId1, mock(ParticipantInfo.class),
			List.of(createResponseData(1L, Map.of("textAnswer", "답변1-2"))));
		Participation p3 = Participation.create(1L, surveyId2, mock(ParticipantInfo.class),
			List.of(createResponseData(2L, Map.of("textAnswer", "답변2"))));

		given(participationRepository.findAllBySurveyIdIn(surveyIds)).willReturn(List.of(p1, p2, p3));

		// when
		List<ParticipationGroupResponse> result = participationService.getAllBySurveyIds(surveyIds);

		// then
		assertThat(result).hasSize(2);

		ParticipationGroupResponse group1 = result.stream()
			.filter(g -> g.getSurveyId().equals(surveyId1))
			.findFirst().orElseThrow();
		assertThat(group1.getParticipations()).hasSize(2);

		ParticipationGroupResponse group2 = result.stream()
			.filter(g -> g.getSurveyId().equals(surveyId2))
			.findFirst().orElseThrow();
		assertThat(group2.getParticipations()).hasSize(1);
	}
}