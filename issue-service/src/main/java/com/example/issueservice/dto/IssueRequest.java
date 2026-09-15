package com.example.issueservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class IssueRequest {

  @NotBlank(message = "title is required")
  private String title;

  @NotBlank(message = "description is required")
  private String description;

  @NotNull(message = "categoryId is required")
  private Long categoryId;

  @NotBlank(message = "area is required")
  private String area;

  private Double latitude;

  private Double longitude;

  private String photoReference;

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
}
