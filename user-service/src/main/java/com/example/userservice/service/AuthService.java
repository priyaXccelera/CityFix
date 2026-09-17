package com.example.userservice.service;

import com.example.userservice.dto.AuthResponse;
import com.example.userservice.dto.LoginRequest;
import com.example.userservice.dto.RegisterRequest;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.entity.AccountStatus;
import com.example.userservice.entity.User;
import com.example.userservice.entity.UserRole;
import com.example.userservice.exception.BadRequestException;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

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

  @Transactional(isolation = Isolation.SERIALIZABLE)
  public AuthResponse register(RegisterRequest request) {
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new BadRequestException("Email already registered: " + request.getEmail());
    }

    UserRole requestedRole =
        request.getRequestedRole() == null ? UserRole.USER : request.getRequestedRole();
    if (requestedRole == UserRole.SUPER_ADMIN
        && userRepository.existsByRole(UserRole.SUPER_ADMIN)) {
      throw new BadRequestException("A Super Admin already exists for this system");
    }

    User user = new User();
    user.setName(request.getName());
    user.setEmail(request.getEmail());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setRole(requestedRole);
    user.setAddress(request.getAddress());
    user.setPhone(request.getPhone());
    boolean pendingAdmin = requestedRole == UserRole.ADMIN;
    user.setActive(!pendingAdmin);
    user.setStatus(pendingAdmin ? AccountStatus.PENDING : AccountStatus.ACTIVE);

    User saved = userRepository.save(user);
    UserResponse responseUser = UserResponse.from(saved);
    if (pendingAdmin) {
      return new AuthResponse(null, 0, responseUser);
    }

    String token =
        jwtUtil.generateToken(
            saved.getId(), saved.getEmail(), saved.getRole().name(), saved.getName());
    return new AuthResponse(token, jwtUtil.getExpirationMs(), responseUser);
  }

  public AuthResponse login(LoginRequest request) {
    User user =
        userRepository
            .findByEmail(request.getEmail())
            .orElseThrow(() -> new BadRequestException("Invalid email or password"));

    if (user.getStatus() == AccountStatus.PENDING) {
      throw new BadRequestException("Account pending Super Admin approval.");
    }
    if (user.getStatus() == AccountStatus.REJECTED) {
      throw new BadRequestException("Your admin registration was rejected.");
    }
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
