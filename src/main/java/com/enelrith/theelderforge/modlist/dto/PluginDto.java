package com.enelrith.theelderforge.modlist.dto;

import java.time.Instant;
import java.util.UUID;

public record PluginDto(UUID id,
                        Instant createdAt,
                        String name,
                        Integer priority,
                        ModDto mod) {
}