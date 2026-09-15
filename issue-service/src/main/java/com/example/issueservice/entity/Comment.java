package com.example.issueservice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
public class Comment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "issue_id", nullable = false)
  private Issue issue;

  @Column(nullable = false, length = 1000)
  private String text;

  @Column(name = "posted_by_user_id", nullable = false)
  private Long postedByUserId;

  @Column(name = "posted_by_name")
  private String postedByName;

  // "ADMIN" or "USER" — role of the poster at the time of posting
  @Column(name = "posted_by_role", nullable = false)
  private String postedByRole;

  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt = LocalDateTime.now();

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Issue getIssue() {
    return issue;
  }

  public void setIssue(Issue issue) {
    this.issue = issue;
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

  public String getPostedByRole() {
    return postedByRole;
  }

  public void setPostedByRole(String postedByRole) {
    this.postedByRole = postedByRole;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
