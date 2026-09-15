package com.example.issueservice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "issues")
public class Issue {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String title;

  @Column(length = 2000)
  private String description;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "category_id", nullable = false)
  private IssueCategory category;

  @Column(nullable = false)
  private String area;

  private Double latitude;

  private Double longitude;

  @Column(name = "photo_reference")
  private String photoReference;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private IssueStatus status = IssueStatus.REPORTED;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Priority priority;

  @Column(name = "reported_by_user_id", nullable = false)
  private Long reportedByUserId;

  @Column(name = "reported_by_name")
  private String reportedByName;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "assigned_department_id")
  private Department assignedDepartment;

  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt = LocalDateTime.now();

  @Column(nullable = false)
  private LocalDateTime updatedAt = LocalDateTime.now();

  private LocalDateTime resolvedAt;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public IssueCategory getCategory() {
    return category;
  }

  public void setCategory(IssueCategory category) {
    this.category = category;
  }

  public String getArea() {
    return area;
  }

  public void setArea(String area) {
    this.area = area;
  }

  public Double getLatitude() {
    return latitude;
  }

  public void setLatitude(Double latitude) {
    this.latitude = latitude;
  }

  public Double getLongitude() {
    return longitude;
  }

  public void setLongitude(Double longitude) {
    this.longitude = longitude;
  }

  public String getPhotoReference() {
    return photoReference;
  }

  public void setPhotoReference(String photoReference) {
    this.photoReference = photoReference;
  }

  public IssueStatus getStatus() {
    return status;
  }

  public void setStatus(IssueStatus status) {
    this.status = status;
  }

  public Priority getPriority() {
    return priority;
  }

  public void setPriority(Priority priority) {
    this.priority = priority;
  }

  public Long getReportedByUserId() {
    return reportedByUserId;
  }

  public void setReportedByUserId(Long reportedByUserId) {
    this.reportedByUserId = reportedByUserId;
  }

  public String getReportedByName() {
    return reportedByName;
  }

  public void setReportedByName(String reportedByName) {
    this.reportedByName = reportedByName;
  }

  public Department getAssignedDepartment() {
    return assignedDepartment;
  }

  public void setAssignedDepartment(Department assignedDepartment) {
    this.assignedDepartment = assignedDepartment;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public LocalDateTime getResolvedAt() {
    return resolvedAt;
  }

  public void setResolvedAt(LocalDateTime resolvedAt) {
    this.resolvedAt = resolvedAt;
  }
}
