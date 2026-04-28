package com.enelrith.theelderforge.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(@Size(message = "{user.email.size}", max = 255)
                                  @Email(message = "{user.email.email}")
                                  @NotBlank(message = "{user.email.notBlank}")
                                  String email,
                                  @Size(message = "{user.password.size}", min = 8, max = 72)
                                  @NotBlank(message = "{user.password.notBlank}")
                                  String password,
                                  @NotBlank
                                  @Size(max = 20)
                                  String username) {
}