package com.example.Backend.service;

import com.example.Backend.dto.AuthResponseDTO;
import com.example.Backend.dto.LoginRequestDTO;
import com.example.Backend.dto.SignupRequestDTO;
import com.example.Backend.dto.UserResponseDTO;

public interface AuthService {

    AuthResponseDTO register(SignupRequestDTO request);

    AuthResponseDTO login(LoginRequestDTO request);

    UserResponseDTO getCurrentUser(String email);
}
