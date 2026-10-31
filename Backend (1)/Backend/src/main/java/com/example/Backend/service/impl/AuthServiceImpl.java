package com.example.Backend.service.impl;

import com.example.Backend.dto.AuthResponseDTO;
import com.example.Backend.dto.LoginRequestDTO;
import com.example.Backend.dto.SignupRequestDTO;
import com.example.Backend.dto.UserResponseDTO;
import com.example.Backend.entity.User;
import com.example.Backend.entity.enums.Role;
import com.example.Backend.exception.ConflictException;
import com.example.Backend.exception.ResourceNotFoundException;
import com.example.Backend.repository.UserRepository;
import com.example.Backend.security.JwtService;
import com.example.Backend.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService,
                           AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    @Transactional
    public AuthResponseDTO register(SignupRequestDTO request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new ConflictException("An account with email '" + normalizedEmail + "' already exists. Please sign in instead.");
        }

        User user = new User();
        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(request.getPassword().trim()));
        user.setRole(Role.ADMIN);

        User savedUser = userRepository.save(user);

        String token = jwtService.generateToken(savedUser);
        UserResponseDTO userResponse = mapToUserResponseDTO(savedUser);

        return new AuthResponseDTO(token, userResponse, jwtService.getExpirationTime());
    }

    @Override
    public AuthResponseDTO login(LoginRequestDTO request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        // Performs Spring Security authentication (verifies email & BCrypt password)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(normalizedEmail, request.getPassword().trim())
        );

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + normalizedEmail));

        String token = jwtService.generateToken(user);
        UserResponseDTO userResponse = mapToUserResponseDTO(user);

        return new AuthResponseDTO(token, userResponse, jwtService.getExpirationTime());
    }

    @Override
    public UserResponseDTO getCurrentUser(String email) {
        String normalizedEmail = email.trim().toLowerCase();
        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + normalizedEmail));
        return mapToUserResponseDTO(user);
    }

    private UserResponseDTO mapToUserResponseDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
