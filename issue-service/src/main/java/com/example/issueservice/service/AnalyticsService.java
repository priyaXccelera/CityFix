package com.example.issueservice.service;

import com.example.issueservice.dto.AnalyticsResponse;
import com.example.issueservice.dto.IssueResponse;
import com.example.issueservice.entity.Issue;
import com.example.issueservice.repository.IssueRepository;
import com.example.issueservice.repository.UpvoteRepository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsService {

  private final IssueRepository issueRepository;
  private final UpvoteRepository upvoteRepository;
  private final IssueService issueService;

  public AnalyticsService(
      IssueRepository issueRepository,
      UpvoteRepository upvoteRepository,
      IssueService issueService) {
    this.issueRepository = issueRepository;
    this.upvoteRepository = upvoteRepository;
    this.issueService = issueService;
  }

  public AnalyticsResponse buildAnalytics(int topLimit) {
    AnalyticsResponse response = new AnalyticsResponse();

    Map<String, Long> statusCounts = new LinkedHashMap<>();
    for (Object[] row : issueRepository.countGroupedByStatus()) {
      statusCounts.put(String.valueOf(row[0]), (Long) row[1]);
    }
    response.setCountsByStatus(statusCounts);

    Map<String, Long> categoryCounts = new LinkedHashMap<>();
    for (Object[] row : issueRepository.countGroupedByCategory()) {
      categoryCounts.put(String.valueOf(row[0]), (Long) row[1]);
    }
    response.setCountsByCategory(categoryCounts);

    response.setAverageResolutionHours(issueRepository.averageResolutionHours());

    List<Long> topIds = upvoteRepository.topUpvotedUnresolvedIssueIds(PageRequest.of(0, topLimit));
    List<Issue> topIssues = issueService.findAllByIds(topIds);
    List<IssueResponse> topResponses =
        topIssues.stream().map(issueService::toResponse).collect(Collectors.toList());
    response.setTopUpvotedUnresolved(topResponses);

    return response;
  }
}
