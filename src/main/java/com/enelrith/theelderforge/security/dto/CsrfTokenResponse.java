package com.enelrith.theelderforge.security.dto;

public record CsrfTokenResponse(String headerName, String parameterName, String token) {
}
