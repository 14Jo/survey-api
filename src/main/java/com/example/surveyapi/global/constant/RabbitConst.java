package com.example.surveyapi.global.constant;

public class RabbitConst {
	public static final String EXCHANGE_NAME = "domain.event.exchange";
	public static final String DELAYED_EXCHANGE_NAME = "domain.event.exchange.delayed";

	public static final String QUEUE_NAME_USER = "queue.user";
	public static final String QUEUE_NAME_SURVEY = "queue.survey";
	public static final String QUEUE_NAME_STATISTIC = "queue.statistic";
	public static final String QUEUE_NAME_SHARE = "queue.share";
	public static final String QUEUE_NAME_PROJECT = "queue.project";
	public static final String QUEUE_NAME_PARTICIPATION = "queue.participation";

	public static final String ROUTING_KEY_SURVEY_ACTIVE = "survey.activated";
	public static final String ROUTING_KEY_SURVEY_START_DUE = "survey.start.due";
	public static final String ROUTING_KEY_SURVEY_END_DUE = "survey.end.due";

	public static final String ROUTING_KEY_PROJECT_ACTIVE = "project.activated";

	public static final String ROUTING_KEY_USER_WITHDRAW = "survey.user.withdraw";
	public static final String ROUTING_KEY_PARTICIPATION_CREATE = "participation.created";
	public static final String ROUTING_KEY_PARTICIPATION_UPDATE = "participation.updated";
}
