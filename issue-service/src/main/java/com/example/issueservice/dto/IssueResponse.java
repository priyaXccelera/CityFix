package com.example.issueservice.dto;

import com.example.issueservice.entity.Issue;
import com.example.issueservice.entity.IssueStatus;
import com.example.issueservice.entity.Priority;
import java.time.LocalDateTime;

public class IssueResponse {
  private Long id;
  private String title;
  private String description;
  private Long categoryId;
  private String categoryName;
  private String area;
  private Double latitude;
  private Double longitude;
  private String photoReference;
  private IssueStatus status;
  private Priority priority;
  private Long reportedByUserId;
  private String reportedByName;
  private Long assignedDepartmentId;
  private String assignedDepartmentName;
  private long upvoteCount;
  private long commentCount;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private LocalDateTime resolvedAt;

  public static IssueResponse from(Issue i, long upvoteCount, long commentCount) {
    IssueResponse r = new IssueResponse();
    r.id = i.getId();
    r.title = i.getTitle();
    r.description = i.getDescription();
    r.categoryId = i.getCategory().getId();
    r.categoryName = i.getCategory().getName();
    r.area = i.getArea();
    r.latitude = i.getLatitude();
    r.longitude = i.getLongitude();
    r.photoReference = i.getPhotoReference();
    r.status = i.getStatus();
    r.priority = i.getPriority();
    r.reportedByUserId = i.getReportedByUserId();
    r.reportedByName = i.getReportedByName();
    if (i.getAssignedDepartment() != null) {
      r.assignedDepartmentId = i.getAssignedDepartment().getId();
      r.assignedDepartmentName = i.getAssignedDepartment().getName();
    }
    r.upvoteCount = upvoteCount;
    r.commentCount = commentCount;
    r.createdAt = i.getCreatedAt();
    r.updatedAt = i.getUpdatedAt();
    r.resolvedAt = i.getResolvedAt();
    return r;
  }

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

  public Long getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(Long categoryId) {
    this.categoryId = categoryId;
  }

  public String getCategoryName() {
    return categoryName;
  }

  public void setCategoryName(String categoryName) {
    this.categoryName = categoryName;
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

  public Long getAssignedDepartmentId() {
    return assignedDepartmentId;
  }

  public void setAssignedDepartmentId(Long assignedDepartmentId) {
    this.assignedDepartmentId = assignedDepartmentId;
  }

  public String getAssignedDepartmentName() {
    return assignedDepartmentName;
  }

  public void setAssignedDepartmentName(String assignedDepartmentName) {
    this.assignedDepartmentName = assignedDepartmentName;
  }

  public long getUpvoteCount() {
    return upvoteCount;
  }

  public void setUpvoteCount(long upvoteCount) {
    this.upvoteCount = upvoteCount;
  }

  public long getCommentCount() {
    return commentCount;
  }

  public void setCommentCount(long commentCount) {
    this.commentCount = commentCount;
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
