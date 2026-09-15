package com.example.adminannouncementservice.dto;

import com.example.adminannouncementservice.entity.Announcement;
import java.time.LocalDateTime;

public class AnnouncementResponse {
  private Long id;
  private String text;
  private Long postedByUserId;
  private String postedByName;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static AnnouncementResponse from(Announcement a) {
    AnnouncementResponse r = new AnnouncementResponse();
    r.id = a.getId();
    r.text = a.getText();
    r.postedByUserId = a.getPostedByUserId();
    r.postedByName = a.getPostedByName();
    r.createdAt = a.getCreatedAt();
    r.updatedAt = a.getUpdatedAt();
    return r;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public Long getPostedByUserId() {
    return postedByUserId;
  }

  public void setPostedByUserId(Long postedByUserId) {
    this.postedByUserId = postedByUserId;
  }

  public String getPostedByName() {
    return postedByName;
  }

  public void setPostedByName(String postedByName) {
    this.postedByName = postedByName;
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
}
