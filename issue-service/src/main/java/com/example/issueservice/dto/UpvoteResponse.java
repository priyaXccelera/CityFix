package com.example.issueservice.dto;

public class UpvoteResponse {
  private Long issueId;
  private long upvoteCount;
  private boolean upvotedByCurrentUser;

  public UpvoteResponse(Long issueId, long upvoteCount, boolean upvotedByCurrentUser) {
    this.issueId = issueId;
    this.upvoteCount = upvoteCount;
    this.upvotedByCurrentUser = upvotedByCurrentUser;
  }

  public Long getIssueId() {
    return issueId;
  }

  public void setIssueId(Long issueId) {
    this.issueId = issueId;
  }

  public long getUpvoteCount() {
    return upvoteCount;
  }

  public void setUpvoteCount(long upvoteCount) {
    this.upvoteCount = upvoteCount;
  }

  public boolean isUpvotedByCurrentUser() {
    return upvotedByCurrentUser;
  }

  public void setUpvotedByCurrentUser(boolean upvotedByCurrentUser) {
    this.upvotedByCurrentUser = upvotedByCurrentUser;
  }
}
