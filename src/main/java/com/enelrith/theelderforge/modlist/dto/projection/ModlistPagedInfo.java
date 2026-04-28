package com.enelrith.theelderforge.modlist.dto.projection;

import com.enelrith.theelderforge.user.dto.projection.UserInfo;

import java.time.Instant;
import java.util.UUID;

public interface ModlistPagedInfo {
    UUID getId();

    Instant getCreatedAt();

    Instant getUpdatedAt();

    String getName();

    UserInfo getUser();
}