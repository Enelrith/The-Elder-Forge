package com.enelrith.theelderforge.user;

import com.enelrith.theelderforge.user.dto.RegisterUserRequest;
import com.enelrith.theelderforge.user.dto.UserDto;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserDto registerUser(RegisterUserRequest request) {
        if (userRepository.existsByEmail(request.email())) throw new EntityExistsException("Email already exists");

        var user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));

        var savedUser = userRepository.saveAndFlush(user);

        return userMapper.toUserDto(savedUser);
    }
}
