package com.enelrith.theelderforge.security;

import com.enelrith.theelderforge.security.dto.AccessJwtResponse;
import com.enelrith.theelderforge.security.dto.AuthRequest;
import com.enelrith.theelderforge.security.dto.JwtResponse;
import com.enelrith.theelderforge.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    public JwtResponse loginUser(AuthRequest request) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        var userDetails = (UserDetails) authentication.getPrincipal();
        return new JwtResponse(jwtService.generateAccessToken(userDetails), jwtService.generateRefreshToken(userDetails));
    }

    public AccessJwtResponse refreshToken(String refreshToken) {
        if (!jwtService.validateToken(refreshToken)) {
            return new AccessJwtResponse("");
        }
        var userEmail = jwtService.getEmailFromToken(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

        return new AccessJwtResponse(jwtService.generateAccessToken(userDetails));
    }
}
