package com.enelrith.theelderforge.user;

import com.enelrith.theelderforge.user.dto.RegisterUserRequest;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private final String testEmail = "test@email.com";
    private final String testPassword = "12345678";
    private final String testHashedPassword = "hashedPassword";

    @Test
    void shouldRegisterUser() {
        var request = new RegisterUserRequest(testEmail, testPassword);
        var user = userMapper.toEntity(request);

        when(userRepository.existsByEmail(testEmail)).thenReturn(false);
        when(passwordEncoder.encode(testPassword)).thenReturn(testHashedPassword);
        when(userRepository.saveAndFlush(any())).thenReturn(user);

        var savedUserDto = userService.registerUser(request);

        assertThat(savedUserDto).hasFieldOrPropertyWithValue("email", testEmail);
        verify(passwordEncoder).encode(testPassword);
        verify(userRepository).saveAndFlush(any());
    }

    @Test
    void shouldThrowEntityExistsException() {
        var request = new RegisterUserRequest(testEmail, testPassword);

        when(userRepository.existsByEmail(testEmail)).thenReturn(true);

        assertThatThrownBy(() -> userService.registerUser(request)).isInstanceOf(EntityExistsException.class);
    }
}
