package com.example.surveyapi.global.constant;

public class RabbitConst {
	public static final String EXCHANGE_NAME = "domain.event.exchange";

	public static final String QUEUE_NAME_USER = "queue.user";
	public static final String QUEUE_NAME_SURVEY = "queue.survey";
	public static final String QUEUE_NAME_STATISTIC = "queue.statistic";
	public static final String QUEUE_NAME_SHARE = "queue.share";
	public static final String QUEUE_NAME_PROJECT = "queue.project";
	public static final String QUEUE_NAME_PARTICIPATION = "queue.participation";

	public static final String ROUTING_KEY_SURVEY_ACTIVE = "survey.activated";
	public static final String ROUTING_KEY_USER_WITHDRAW = "survey.user.withdraw";
	public static final String ROUTING_KEY_PARTICIPATION_CREATE = "participation.created";
	public static final String ROUTING_KEY_PARTICIPATION_UPDATE = "participation.updated";
	public static final String ROUTING_KEY_PROJECT_STATE_CHANGED = "project.state";
	public static final String ROUTING_KEY_PROJECT_DELETED = "project.deleted";
	public static final String ROUTING_KEY_ADD_MANAGER = "project.manager";
	public static final String ROUTING_KEY_ADD_MEMBER = "project.member";
}
