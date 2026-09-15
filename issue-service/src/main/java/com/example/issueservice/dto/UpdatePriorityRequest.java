package com.example.issueservice.dto;

import com.example.issueservice.entity.Priority;
import jakarta.validation.constraints.NotNull;

public class UpdatePriorityRequest {

  @NotNull(message = "priority is required")
  private Priority priority;

  public Priority getPriority() {
    return priority;
  }

  public void setPriority(Priority priority) {
    this.priority = priority;
  }
}
