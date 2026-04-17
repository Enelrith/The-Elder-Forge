package com.enelrith.theelderforge.user;

import com.enelrith.theelderforge.user.dto.RegisterUserRequest;
import com.enelrith.theelderforge.user.dto.UserDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> registerUser(@RequestBody @Valid RegisterUserRequest request) {
        var userDto = userService.registerUser(request);
        var uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(userDto.id())
                .toUri();
        return ResponseEntity.created(uri).body(userDto);
    }
}
