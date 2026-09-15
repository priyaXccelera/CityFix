package com.example.issueservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "issue_categories")
public class IssueCategory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String name;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "department_id", nullable = false)
  private Department department;

  @Enumerated(EnumType.STRING)
  @Column(name = "default_priority", nullable = false)
  private Priority defaultPriority;

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

  public Department getDepartment() {
    return department;
  }

  public void setDepartment(Department department) {
    this.department = department;
  }

  public Priority getDefaultPriority() {
    return defaultPriority;
  }

  public void setDefaultPriority(Priority defaultPriority) {
    this.defaultPriority = defaultPriority;
  }
}
