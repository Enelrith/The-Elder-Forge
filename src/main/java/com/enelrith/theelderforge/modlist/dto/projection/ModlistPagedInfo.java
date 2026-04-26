package com.enelrith.theelderforge.modlist.dto.projection;

import java.time.Instant;
import java.util.UUID;

public interface ModlistPagedInfo {
    UUID getId();

    Instant getCreatedAt();

    Instant getUpdatedAt();

    String getName();

    UserInfo getUser();
}