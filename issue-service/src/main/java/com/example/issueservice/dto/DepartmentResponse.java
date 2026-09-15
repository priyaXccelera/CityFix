package com.example.issueservice.dto;

import com.example.issueservice.entity.Department;

public class DepartmentResponse {
  private Long id;
  private String name;
  private String description;
  private String categoryHandled;

  public static DepartmentResponse from(Department d) {
    DepartmentResponse r = new DepartmentResponse();
    r.id = d.getId();
    r.name = d.getName();
    r.description = d.getDescription();
    r.categoryHandled = d.getCategoryHandled();
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
