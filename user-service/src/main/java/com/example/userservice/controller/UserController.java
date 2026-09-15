package com.example.userservice.controller;

import com.example.userservice.dto.UserResponse;
import com.example.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "Admin-only user management")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping
  @Operation(summary = "List all users, paginated (ADMIN only)")
  public ResponseEntity<Page<UserResponse>> listUsers(
      @PageableDefault(size = 20) Pageable pageable) {
    return ResponseEntity.ok(userService.listUsers(pageable));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get a single user by id (ADMIN only)")
  public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
    return ResponseEntity.ok(userService.getUser(id));
  }

  @PutMapping("/{id}/deactivate")
  @Operation(summary = "Deactivate a user account (ADMIN only)")
  public ResponseEntity<UserResponse> deactivateUser(@PathVariable Long id) {
    return ResponseEntity.ok(userService.deactivateUser(id));
  }
}
