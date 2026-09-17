package com.example.userservice.controller;

import com.example.userservice.dto.CreateAdminRequest;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "ADMIN user management and SUPER_ADMIN-only admin creation")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/admins")
  @Operation(summary = "Create an ADMIN account (SUPER_ADMIN-ONLY; requires a valid SUPER_ADMIN JWT)")
  public ResponseEntity<UserResponse> createAdmin(@Valid @RequestBody CreateAdminRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(userService.createAdmin(request));
  }

  @GetMapping("/admins/pending")
  @Operation(summary = "SUPER_ADMIN-ONLY: List pending ADMIN registration requests")
  public ResponseEntity<Page<UserResponse>> listPendingAdmins(
      @PageableDefault(size = 20) Pageable pageable) {
    return ResponseEntity.ok(userService.listPendingAdmins(pageable));
  }

  @PutMapping("/admins/{id}/approve")
  @Operation(summary = "SUPER_ADMIN-ONLY: Approve a pending ADMIN registration")
  public ResponseEntity<UserResponse> approvePendingAdmin(@PathVariable Long id) {
    return ResponseEntity.ok(userService.approvePendingAdmin(id));
  }

  @PutMapping("/admins/{id}/reject")
  @Operation(summary = "SUPER_ADMIN-ONLY: Reject a pending ADMIN registration")
  public ResponseEntity<UserResponse> rejectPendingAdmin(@PathVariable Long id) {
    return ResponseEntity.ok(userService.rejectPendingAdmin(id));
  }

  @GetMapping
  @Operation(summary = "ADMIN: List all users, paginated (ADMIN and SUPER_ADMIN)")
  public ResponseEntity<Page<UserResponse>> listUsers(
      @PageableDefault(size = 20) Pageable pageable) {
    return ResponseEntity.ok(userService.listUsers(pageable));
  }

  @GetMapping("/{id}")
  @Operation(summary = "ADMIN: Get a single user by id (ADMIN and SUPER_ADMIN)")
  public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
    return ResponseEntity.ok(userService.getUser(id));
  }

  @PutMapping("/{id}/deactivate")
  @Operation(summary = "Deactivate a USER (ADMIN) or ADMIN (SUPER_ADMIN-ONLY) account")
  public ResponseEntity<UserResponse> deactivateUser(
      @PathVariable Long id, Authentication authentication) {
    boolean isSuperAdmin =
        authentication.getAuthorities().stream()
            .anyMatch(authority -> "ROLE_SUPER_ADMIN".equals(authority.getAuthority()));
    return ResponseEntity.ok(userService.deactivateUser(id, isSuperAdmin));
  }
}
