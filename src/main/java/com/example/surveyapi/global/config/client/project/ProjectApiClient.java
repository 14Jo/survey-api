package com.example.surveyapi.global.config.client.project;

import java.util.List;

import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;


import com.example.surveyapi.global.config.client.ExternalApiResponse;

@HttpExchange
public interface ProjectApiClient {

    @GetExchange("/api/v1/projects/me")
    ExternalApiResponse getProjectMyRole(
        @RequestHeader("Authorization") String authHeader,
        @RequestParam Long userId);
}
