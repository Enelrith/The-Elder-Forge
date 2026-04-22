package com.enelrith.theelderforge.modlist.dto;

import com.enelrith.theelderforge.user.dto.UserDto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ModlistDto(UUID id, Instant createdAt, String name, String description, Boolean isPublic, UserDto user, List<ModDto> mods) {
}