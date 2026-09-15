package com.example.issueservice.dto;

import jakarta.validation.constraints.NotBlank;

public class DepartmentRequest {

  @NotBlank(message = "name is required")
  private String name;

  private String description;

  private String categoryHandled;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getCategoryHandled() {
    return categoryHandled;
  }

  public void setCategoryHandled(String categoryHandled) {
    this.categoryHandled = categoryHandled;
  }
}
