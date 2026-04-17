package com.enelrith.theelderforge.shared;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Map;

@AllArgsConstructor
@Slf4j
@Getter
@Schema(description = "Returned when an exception that is handled by GlobalExceptionHandler is thrown")
public class ErrorResponse {
    @Schema(description = "The timestamp of when the exception was thrown", example = "2026-03-18T00:47:53.990137900Z")
    private Instant timestamp;
    @Schema(description = "The status code of the exception", example = "400")
    private Integer status;
    @Schema(description = "The message of the exception", example = "A validation error has occurred in one or more fields")
    private String message;
    @Schema(description = "The error type of the exception", example = "Bad Request")
    private String error;
    @Schema(description = "The path the exception was thrown in", example = "api/v1/auth")
    private String path;
    @Schema(description = "Map that contains all the validation errors of the request. This field is null if the exception has no validation errors",
            example = "{\n" +
                    "        \"password\": \"The password must be between 8 and 72 characters long\",\n" +
                    "        \"email\": \"Invalid email\"\n" +
                    "    }"
    )
    private Map<String, String> validationErrors;

    public static ErrorResponse buildErrorResponse(Instant timestamp, Integer status, String message, String error, String path) {
        log.warn("An exception has occurred for path {}, status {}, message: {}, error: {}", path, status, message, error);

        return new ErrorResponse(timestamp, status, message, error, path, null);
    }

    public static ErrorResponse buildValidationErrorResponse(Instant timestamp, Integer status, String error, String path, Map<String, String> validationErrors) {
        log.warn("A validation error has occurred for path {}, status {}, error: {}, validation errors: {}", path, status, error, validationErrors);

        return new ErrorResponse(timestamp, status, "A validation error has occurred in one or more fields", error, path, validationErrors);
    }
}