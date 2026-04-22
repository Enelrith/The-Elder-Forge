package com.enelrith.theelderforge.modlist.dto;

import java.time.Instant;
import java.util.UUID;

public record CategoryDto(UUID id, Instant createdAt, Integer nexusId, String name) {
}