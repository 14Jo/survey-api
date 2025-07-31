package com.example.surveyapi.global.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PageInfo {
    private final int size;
    private final int number;
    private final long totalElements;
    private final int totalPages;
}