package com.sandesh.clinicapp.service;

import com.sandesh.clinicapp.dto.*;
import com.sandesh.clinicapp.exception.InvalidTokenException;
import com.sandesh.clinicapp.model.RefreshToken;
import com.sandesh.clinicapp.model.User;
import com.sandesh.clinicapp.repository.RefreshTokenRepository;
import com.sandesh.clinicapp.repository.UserRepository;
import com.sandesh.clinicapp.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        userRepository.save(user);

        return issueTokens(user);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        return issueTokens(user);
    }

    public AuthResponse refresh(String refreshToken) {
        String email = jwtService.extractEmail(refreshToken);
        String hashedIncoming = jwtService.hashToken(refreshToken);

        RefreshToken storedToken = refreshTokenRepository.findByTokenHash(hashedIncoming)
                .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));

        if (storedToken.isRevoked() || storedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Refresh token expired or revoked");
        }


        User user = storedToken.getUser();

        storedToken.setRevoked(true);
        refreshTokenRepository.save(storedToken);

        return issueTokens(user);
    }

    private AuthResponse issueTokens(User user) {
        String accessToken = jwtService.generateToken(user.getEmail());
        String refreshTokenRaw = jwtService.generateRefreshToken(user.getEmail());

        RefreshToken tokenEntity = new RefreshToken();
        tokenEntity.setUser(user);
        tokenEntity.setTokenHash(jwtService.hashToken(refreshTokenRaw));
        tokenEntity.setExpiresAt(LocalDateTime.now().plusDays(7));
        tokenEntity.setRevoked(false);
        refreshTokenRepository.save(tokenEntity);

        return new AuthResponse(accessToken, refreshTokenRaw);
    }
}