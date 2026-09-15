package com.example.issueservice.dto;

import com.example.issueservice.entity.Comment;
import java.time.LocalDateTime;

public class CommentResponse {
  private Long id;
  private Long issueId;
  private String text;
  private Long postedByUserId;
  private String postedByName;
  private String postedByRole;
  private LocalDateTime createdAt;

  public static CommentResponse from(Comment c) {
    CommentResponse r = new CommentResponse();
    r.id = c.getId();
    r.issueId = c.getIssue().getId();
    r.text = c.getText();
    r.postedByUserId = c.getPostedByUserId();
    r.postedByName = c.getPostedByName();
    r.postedByRole = c.getPostedByRole();
    r.createdAt = c.getCreatedAt();
    return r;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getIssueId() {
    return issueId;
  }

  public void setIssueId(Long issueId) {
    this.issueId = issueId;
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
