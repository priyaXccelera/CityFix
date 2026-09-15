package com.example.issueservice.dto;

import com.example.issueservice.entity.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CategoryRequest {

  @NotBlank(message = "name is required")
  private String name;

  @NotNull(message = "departmentId is required")
  private Long departmentId;

  @NotNull(message = "defaultPriority is required")
  private Priority defaultPriority;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Long getDepartmentId() {
    return departmentId;
  }

  public void setDepartmentId(Long departmentId) {
    this.departmentId = departmentId;
  }

  public Priority getDefaultPriority() {
    return defaultPriority;
  }

  public void setDefaultPriority(Priority defaultPriority) {
    this.defaultPriority = defaultPriority;
  }
}
