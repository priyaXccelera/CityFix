package com.example.userservice.service;

import com.example.userservice.dto.CreateAdminRequest;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.entity.AccountStatus;
import com.example.userservice.entity.User;
import com.example.userservice.entity.UserRole;
import com.example.userservice.exception.BadRequestException;
import com.example.userservice.exception.ResourceNotFoundException;
import com.example.userservice.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public UserResponse createAdmin(CreateAdminRequest request) {
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new BadRequestException("Email already registered: " + request.getEmail());
    }

    User user = new User();
    user.setName(request.getName());
    user.setEmail(request.getEmail());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setRole(UserRole.ADMIN);
    user.setActive(true);
    user.setStatus(AccountStatus.ACTIVE);
    return UserResponse.from(userRepository.save(user));
  }

  public Page<UserResponse> listUsers(Pageable pageable) {
    return userRepository.findAll(pageable).map(UserResponse::from);
  }

  public UserResponse getUser(Long id) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    return UserResponse.from(user);
  }

  public Page<UserResponse> listPendingAdmins(Pageable pageable) {
    return userRepository
        .findByRoleAndStatus(UserRole.ADMIN, AccountStatus.PENDING, pageable)
        .map(UserResponse::from);
  }

  public UserResponse approvePendingAdmin(Long id) {
    return updatePendingAdminStatus(id, AccountStatus.ACTIVE);
  }

  public UserResponse rejectPendingAdmin(Long id) {
    return updatePendingAdminStatus(id, AccountStatus.REJECTED);
  }

  private UserResponse updatePendingAdminStatus(Long id, AccountStatus status) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    if (user.getRole() != UserRole.ADMIN || user.getStatus() != AccountStatus.PENDING) {
      throw new BadRequestException("User is not a pending ADMIN account: " + id);
    }
    user.setStatus(status);
    user.setActive(status == AccountStatus.ACTIVE);
    return UserResponse.from(userRepository.save(user));
  }

  public UserResponse deactivateUser(Long id, boolean isSuperAdmin) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));

    if (user.getRole() == UserRole.SUPER_ADMIN) {
      throw new AccessDeniedException("The SUPER_ADMIN account cannot be deactivated");
    }
    if (user.getRole() == UserRole.ADMIN && !isSuperAdmin) {
      throw new AccessDeniedException("Only SUPER_ADMIN can deactivate an ADMIN account");
    }

    user.setActive(false);
    return UserResponse.from(userRepository.save(user));
  }
}
