package com.example.issueservice.dto;

import com.example.issueservice.entity.IssueCategory;
import com.example.issueservice.entity.Priority;

public class CategoryResponse {
  private Long id;
  private String name;
  private Long departmentId;
  private String departmentName;
  private Priority defaultPriority;

  public static CategoryResponse from(IssueCategory c) {
    CategoryResponse r = new CategoryResponse();
    r.id = c.getId();
    r.name = c.getName();
    r.departmentId = c.getDepartment().getId();
    r.departmentName = c.getDepartment().getName();
    r.defaultPriority = c.getDefaultPriority();
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

  public Long getDepartmentId() {
    return departmentId;
  }

  public void setDepartmentId(Long departmentId) {
    this.departmentId = departmentId;
  }

  public String getDepartmentName() {
    return departmentName;
  }

  public void setDepartmentName(String departmentName) {
    this.departmentName = departmentName;
  }

  public Priority getDefaultPriority() {
    return defaultPriority;
  }

  public void setDefaultPriority(Priority defaultPriority) {
    this.defaultPriority = defaultPriority;
  }
}
