package com.property.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class FileUrlUtilsTest {

    @Test
    void shouldExtractUploadsPathFromAbsoluteUrl() {
        assertEquals(
                "/uploads/image/2026/05/example.jpg",
                FileUrlUtils.normalizeUploadUrl("http://localhost:8080/uploads/image/2026/05/example.jpg")
        );
    }

    @Test
    void shouldKeepRelativeUploadsPathUntouched() {
        assertEquals(
                "/uploads/avatar/2026/05/example.png",
                FileUrlUtils.normalizeUploadUrl("/uploads/avatar/2026/05/example.png")
        );
    }

    @Test
    void shouldNormalizeCommaSeparatedUploadUrls() {
        assertEquals(
                "/uploads/image/2026/05/a.jpg,/uploads/image/2026/05/b.jpg",
                FileUrlUtils.normalizeUploadUrlList(
                        "http://localhost:8080/uploads/image/2026/05/a.jpg, /uploads/image/2026/05/b.jpg"
                )
        );
    }

    @Test
    void shouldReturnNullWhenInputIsNull() {
        assertNull(FileUrlUtils.normalizeUploadUrl(null));
        assertNull(FileUrlUtils.normalizeUploadUrlList(null));
    }
}
