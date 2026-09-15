package com.example.userservice.service;

import com.example.userservice.dto.UserResponse;
import com.example.userservice.entity.User;
import com.example.userservice.exception.ResourceNotFoundException;
import com.example.userservice.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
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

  public UserResponse deactivateUser(Long id) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    user.setActive(false);
    return UserResponse.from(userRepository.save(user));
  }
}
