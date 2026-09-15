package com.example.adminannouncementservice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "announcements")
public class Announcement {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 2000)
  private String text;

  @Column(name = "posted_by_user_id", nullable = false)
  private Long postedByUserId;

  @Column(name = "posted_by_name")
  private String postedByName;

  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt = LocalDateTime.now();

  @Column(nullable = false)
  private LocalDateTime updatedAt = LocalDateTime.now();

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
