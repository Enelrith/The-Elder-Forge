package com.enelrith.theelderforge.security;

import com.enelrith.theelderforge.security.dto.AccessJwtResponse;
import com.enelrith.theelderforge.security.dto.AuthRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    @Value("${spring.cookies.secure}")
    private boolean isCookieSecure;

    @Value("${spring.security.jwt.expiration-refresh-ms}")
    private long refreshExpirationMs;

    @PostMapping
    public ResponseEntity<AccessJwtResponse> loginUser(@RequestBody AuthRequest request, HttpServletResponse response) {
        var jwtResponse =  authService.loginUser(request);
        var refreshCookie = createRefreshTokenCookie(jwtResponse.refreshToken(), refreshExpirationMs);

        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return ResponseEntity.ok(new AccessJwtResponse(jwtResponse.accessToken()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AccessJwtResponse> refreshToken(@CookieValue(name = "refreshToken") String refreshToken, HttpServletResponse response) {
        var accessJwtResponse = authService.refreshToken(refreshToken);
        if (accessJwtResponse.accessToken().isBlank()) {
            var refreshCookie = createRefreshTokenCookie("", 0);
            response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(accessJwtResponse);
    }

    private ResponseCookie createRefreshTokenCookie(String token, long expirationMs) {
        return ResponseCookie.from("refreshToken", token)
                .httpOnly(true)
                .secure(isCookieSecure)
                .path("/")
                .maxAge(expirationMs / 1000)
                .sameSite("Strict")
                .build();
    }
}
