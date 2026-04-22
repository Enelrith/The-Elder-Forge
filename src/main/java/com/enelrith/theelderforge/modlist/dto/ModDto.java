package com.enelrith.theelderforge.modlist.dto;

import java.time.Instant;
import java.util.UUID;

public record ModDto(UUID id, Instant createdAt, String name, String notes, Integer priority, CategoryDto category) {
}