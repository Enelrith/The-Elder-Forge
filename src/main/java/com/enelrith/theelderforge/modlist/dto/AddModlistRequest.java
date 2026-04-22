package com.enelrith.theelderforge.modlist.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AddModlistRequest(@Size(max = 255)
                                @NotBlank
                                String name,
                                @Size(max = 5000)
                                String description,
                                @NotNull 
                                Boolean isPublic) {
}