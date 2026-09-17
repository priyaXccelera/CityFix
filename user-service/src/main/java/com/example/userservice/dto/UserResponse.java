package com.example.userservice.dto;

import com.example.userservice.entity.AccountStatus;
import com.example.userservice.entity.User;
import com.example.userservice.entity.UserRole;

public class UserResponse {

  private Long id;
  private String name;
  private String email;
  private UserRole role;
  private String address;
  private String phone;
  private boolean active;
  private AccountStatus status;

  public static UserResponse from(User u) {
    UserResponse r = new UserResponse();
    r.id = u.getId();
    r.name = u.getName();
    r.email = u.getEmail();
    r.role = u.getRole();
    r.address = u.getAddress();
    r.phone = u.getPhone();
    r.active = u.isActive();
    r.status = u.getStatus();
    return r;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public UserRole getRole() {
    return role;
  }

  public void setRole(UserRole role) {
    this.role = role;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public AccountStatus getStatus() {
    return status;
  }

  public void setStatus(AccountStatus status) {
    this.status = status;
  }
}
