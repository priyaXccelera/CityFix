package com.example.userservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.userservice.dto.UserResponse;
import com.example.userservice.entity.User;
import com.example.userservice.entity.UserRole;
import com.example.userservice.exception.ResourceNotFoundException;
import com.example.userservice.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock private UserRepository userRepository;

  @InjectMocks private UserService userService;

  @Test
  void getUser_notFound_throwsResourceNotFound() {
    when(userRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> userService.getUser(999L));
  }

  @Test
  void getUser_found_returnsMappedResponse() {
    User user = new User();
    user.setId(5L);
    user.setName("Carla Citizen");
    user.setEmail("carla@cityfix.com");
    user.setRole(UserRole.USER);
    user.setActive(true);

    when(userRepository.findById(5L)).thenReturn(Optional.of(user));

    UserResponse response = userService.getUser(5L);

    assertEquals(5L, response.getId());
    assertEquals("carla@cityfix.com", response.getEmail());
    assertTrue(response.isActive());
  }

  @Test
  void deactivateUser_setsActiveFalseAndSaves() {
    User user = new User();
    user.setId(5L);
    user.setActive(true);

    when(userRepository.findById(5L)).thenReturn(Optional.of(user));
    when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

    UserResponse response = userService.deactivateUser(5L, false);

    assertFalse(response.isActive());
    verify(userRepository).save(user);
  }

  @Test
  void deactivateUser_notFound_throwsResourceNotFound() {
    when(userRepository.findById(123L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> userService.deactivateUser(123L, false));
  }
}
