package com.enelrith.theelderforge.user.dto;

import java.time.Instant;
import java.util.UUID;

public record UserDto(UUID id, Instant createdAt, String email, String username) {
}