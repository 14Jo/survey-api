package com.example.surveyapi.domain.share.application.event.port;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.share.application.event.dto.ShareCreateRequest;
import com.example.surveyapi.domain.share.application.event.dto.ShareDeleteRequest;
import com.example.surveyapi.domain.share.application.share.ShareService;
import com.example.surveyapi.domain.share.domain.share.entity.Share;
import com.example.surveyapi.domain.share.domain.share.vo.ShareSourceType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShareEventHandler implements ShareEventPort {
	private final ShareService shareService;

	@Override
	public void handleSurveyEvent(ShareCreateRequest request) {
		shareService.createShare(
			ShareSourceType.SURVEY,
			request.getSourceId(),
			request.getCreatorId(),
			request.getExpirationDate()
		);
	}

	@Override
	public void handleProjectCreateEvent(ShareCreateRequest request) {
		shareService.createShare(
			ShareSourceType.PROJECT_MANAGER,
			request.getSourceId(),
			request.getCreatorId(),
			request.getExpirationDate()
		);
		shareService.createShare(
			ShareSourceType.PROJECT_MEMBER,
			request.getSourceId(),
			request.getCreatorId(),
			request.getExpirationDate()
		);
	}

	@Override
	public void handleProjectManagerEvent(ShareCreateRequest request) {
		shareService.createShare(
			ShareSourceType.PROJECT_MANAGER,
			request.getSourceId(),
			request.getCreatorId(),
			request.getExpirationDate()
		);
	}

	@Override
	public void handleProjectMemberEvent(ShareCreateRequest request) {
		shareService.createShare(
			ShareSourceType.PROJECT_MEMBER,
			request.getSourceId(),
			request.getCreatorId(),
			request.getExpirationDate()
		);
	}

	@Override
	public void handleProjectDeleteEvent(ShareDeleteRequest request) {
		List<Share> shares = shareService.getShareBySourceId(request.getProjectId());

		for (Share share : shares) {
			shareService.delete(share.getId(), request.getDeleterId());
		}
	}
}
