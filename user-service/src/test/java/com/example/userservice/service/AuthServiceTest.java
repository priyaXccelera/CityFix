package com.example.userservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.userservice.dto.AuthResponse;
import com.example.userservice.dto.LoginRequest;
import com.example.userservice.dto.RegisterRequest;
import com.example.userservice.entity.User;
import com.example.userservice.entity.UserRole;
import com.example.userservice.exception.BadRequestException;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.security.JwtUtil;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock private UserRepository userRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @Mock private JwtUtil jwtUtil;

  @InjectMocks private AuthService authService;

  private RegisterRequest registerRequest;

  @BeforeEach
  void setUp() {
    registerRequest = new RegisterRequest();
    registerRequest.setName("Test User");
    registerRequest.setEmail("test@cityfix.com");
    registerRequest.setPassword("Password123");
    registerRequest.setAddress("Downtown");
    registerRequest.setPhone("555-9999");
  }

  @Test
  void register_withDuplicateEmail_throwsBadRequest() {
    when(userRepository.existsByEmail("test@cityfix.com")).thenReturn(true);

    assertThrows(BadRequestException.class, () -> authService.register(registerRequest));
    verify(userRepository, never()).save(any());
  }

  @Test
  void register_withValidData_savesUserAndReturnsToken() {
    when(userRepository.existsByEmail("test@cityfix.com")).thenReturn(false);
    when(passwordEncoder.encode("Password123")).thenReturn("hashed-password");
    when(userRepository.save(any(User.class)))
        .thenAnswer(
            invocation -> {
              User u = invocation.getArgument(0);
              u.setId(42L);
              return u;
            });
    when(jwtUtil.generateToken(eq(42L), eq("test@cityfix.com"), eq("USER"), eq("Test User")))
        .thenReturn("signed-jwt-token");
    when(jwtUtil.getExpirationMs()).thenReturn(1800000L);

    AuthResponse response = authService.register(registerRequest);

    assertEquals("signed-jwt-token", response.getToken());
    assertEquals(UserRole.USER, response.getUser().getRole());
    assertEquals("test@cityfix.com", response.getUser().getEmail());
    verify(userRepository).save(any(User.class));
  }

  @Test
  void login_withWrongPassword_throwsBadRequest() {
    User user = new User();
    user.setId(1L);
    user.setEmail("bob@cityfix.com");
    user.setPassword("hashed");
    user.setRole(UserRole.USER);
    user.setActive(true);

    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setEmail("bob@cityfix.com");
    loginRequest.setPassword("wrongpass");

    when(userRepository.findByEmail("bob@cityfix.com")).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("wrongpass", "hashed")).thenReturn(false);

    assertThrows(BadRequestException.class, () -> authService.login(loginRequest));
  }

  @Test
  void login_withDeactivatedAccount_throwsBadRequest() {
    User user = new User();
    user.setId(1L);
    user.setEmail("eve@cityfix.com");
    user.setPassword("hashed");
    user.setRole(UserRole.USER);
    user.setActive(false);

    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setEmail("eve@cityfix.com");
    loginRequest.setPassword("Password123");

    when(userRepository.findByEmail("eve@cityfix.com")).thenReturn(Optional.of(user));

    assertThrows(BadRequestException.class, () -> authService.login(loginRequest));
    verify(passwordEncoder, never()).matches(any(), any());
  }

  @Test
  void login_withValidCredentials_returnsToken() {
    User user = new User();
    user.setId(1L);
    user.setName("Bob Reporter");
    user.setEmail("bob@cityfix.com");
    user.setPassword("hashed");
    user.setRole(UserRole.USER);
    user.setActive(true);

    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setEmail("bob@cityfix.com");
    loginRequest.setPassword("Password123");

    when(userRepository.findByEmail("bob@cityfix.com")).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("Password123", "hashed")).thenReturn(true);
    when(jwtUtil.generateToken(1L, "bob@cityfix.com", "USER", "Bob Reporter"))
        .thenReturn("token-xyz");
    when(jwtUtil.getExpirationMs()).thenReturn(1800000L);

    AuthResponse response = authService.login(loginRequest);

    assertEquals("token-xyz", response.getToken());
    assertEquals("bob@cityfix.com", response.getUser().getEmail());
  }
}
