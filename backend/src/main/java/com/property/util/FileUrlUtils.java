package com.property.util;

import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 文件URL处理工具
 */
public final class FileUrlUtils {

    private static final String UPLOADS_PREFIX = "/uploads/";

    private FileUrlUtils() {
    }

    public static String normalizeUploadUrl(String url) {
        if (!StringUtils.hasText(url)) {
            return url;
        }
        String trimmed = url.trim();
        int uploadsIndex = trimmed.indexOf(UPLOADS_PREFIX);
        if (uploadsIndex >= 0) {
            return trimmed.substring(uploadsIndex);
        }
        return trimmed;
    }

    public static String normalizeUploadUrlList(String urls) {
        if (!StringUtils.hasText(urls)) {
            return urls;
        }
        return Arrays.stream(urls.split(","))
                .map(FileUrlUtils::normalizeUploadUrl)
                .filter(StringUtils::hasText)
                .collect(Collectors.joining(","));
    }
}
