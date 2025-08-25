package com.example.surveyapi.global.util;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

public final class RepositorySliceUtil {

    private RepositorySliceUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static Pageable createPageable(int page, int size) {
        return PageRequest.of(page, size);
    }

    public static Pageable createPageableWithDefault(int page, int size, int defaultSize) {
        int actualSize = size > 0 ? size : defaultSize;
        return PageRequest.of(page, actualSize);
    }

    public static <T> Slice<T> toSlice(List<T> content, Pageable pageable) {
        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            hasNext = true;
            content = content.subList(0, pageable.getPageSize());
        }
        return new SliceImpl<>(content, pageable, hasNext);
    }
}