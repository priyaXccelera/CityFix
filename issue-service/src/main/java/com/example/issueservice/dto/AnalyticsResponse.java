package com.example.issueservice.dto;

import java.util.List;
import java.util.Map;

public class AnalyticsResponse {
  private Map<String, Long> countsByStatus;
  private Map<String, Long> countsByCategory;
  private Double averageResolutionHours;
  private List<IssueResponse> topUpvotedUnresolved;

  public Map<String, Long> getCountsByStatus() {
    return countsByStatus;
  }

  public void setCountsByStatus(Map<String, Long> countsByStatus) {
    this.countsByStatus = countsByStatus;
  }

  public Map<String, Long> getCountsByCategory() {
    return countsByCategory;
  }

  public void setCountsByCategory(Map<String, Long> countsByCategory) {
    this.countsByCategory = countsByCategory;
  }

  public Double getAverageResolutionHours() {
    return averageResolutionHours;
  }

  public void setAverageResolutionHours(Double averageResolutionHours) {
    this.averageResolutionHours = averageResolutionHours;
  }

  public List<IssueResponse> getTopUpvotedUnresolved() {
    return topUpvotedUnresolved;
  }

  public void setTopUpvotedUnresolved(List<IssueResponse> topUpvotedUnresolved) {
    this.topUpvotedUnresolved = topUpvotedUnresolved;
  }
}
