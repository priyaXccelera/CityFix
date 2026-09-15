package com.example.issueservice.dto;

import jakarta.validation.constraints.NotNull;

public class AssignDepartmentRequest {

  @NotNull(message = "departmentId is required")
  private Long departmentId;

  public Long getDepartmentId() {
    return departmentId;
  }

  public void setDepartmentId(Long departmentId) {
    this.departmentId = departmentId;
  }
}
