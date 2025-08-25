package com.example.surveyapi.survey.application.event.command;

public interface EventCommand {

	void execute() throws Exception;

	void compensate(Exception cause);

	String getCommandId();
}
