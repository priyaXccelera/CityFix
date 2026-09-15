package com.example.userservice.service;

import com.example.userservice.dto.AuthResponse;
import com.example.userservice.dto.LoginRequest;
import com.example.userservice.dto.RegisterRequest;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.entity.User;
import com.example.userservice.entity.UserRole;
import com.example.userservice.exception.BadRequestException;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;

  public AuthService(
      UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtUtil = jwtUtil;
  }

  public AuthResponse register(RegisterRequest request) {
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new BadRequestException("Email already registered: " + request.getEmail());
    }

    User user = new User();
    user.setName(request.getName());
    user.setEmail(request.getEmail());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setRole(request.getRole() != null ? request.getRole() : UserRole.USER);
    user.setAddress(request.getAddress());
    user.setPhone(request.getPhone());
    user.setActive(true);

    User saved = userRepository.save(user);

    String token =
        jwtUtil.generateToken(
            saved.getId(), saved.getEmail(), saved.getRole().name(), saved.getName());
    return new AuthResponse(token, jwtUtil.getExpirationMs(), UserResponse.from(saved));
  }

  public AuthResponse login(LoginRequest request) {
    User user =
        userRepository
            .findByEmail(request.getEmail())
            .orElseThrow(() -> new BadRequestException("Invalid email or password"));

    if (!user.isActive()) {
      throw new BadRequestException("This account has been deactivated");
    }

    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      throw new BadRequestException("Invalid email or password");
    }

    String token =
        jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().name(), user.getName());
    return new AuthResponse(token, jwtUtil.getExpirationMs(), UserResponse.from(user));
  }
}
