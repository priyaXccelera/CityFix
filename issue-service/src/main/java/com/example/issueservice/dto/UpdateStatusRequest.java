package com.example.issueservice.dto;

import com.example.issueservice.entity.IssueStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateStatusRequest {

  @NotNull(message = "status is required")
  private IssueStatus status;

  public IssueStatus getStatus() {
    return status;
  }

  public void setStatus(IssueStatus status) {
    this.status = status;
  }
}
