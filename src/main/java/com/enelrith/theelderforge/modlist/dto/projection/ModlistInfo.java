package com.enelrith.theelderforge.modlist.dto.projection;

import java.time.Instant;
import java.util.UUID;

public interface ModlistInfo {
    UUID getId();

    Instant getCreatedAt();

    String getName();

    Boolean getIsPublic();
}