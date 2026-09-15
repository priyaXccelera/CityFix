package com.example.adminannouncementservice.controller;

import com.example.adminannouncementservice.service.AnalyticsAggregationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/analytics")
@Tag(
    name = "Analytics",
    description = "Admin-only aggregated analytics, sourced from issue-service")
public class AnalyticsController {

  private final AnalyticsAggregationService analyticsAggregationService;

  public AnalyticsController(AnalyticsAggregationService analyticsAggregationService) {
    this.analyticsAggregationService = analyticsAggregationService;
  }

  @GetMapping
  @Operation(
      summary =
          "Aggregated analytics: issue counts by status/category, average resolution time, most"
              + " upvoted unresolved issues (ADMIN only)")
  public ResponseEntity<Map<String, Object>> analytics(
      @RequestParam(defaultValue = "5") int topLimit, HttpServletRequest request) {
    String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    return ResponseEntity.ok(analyticsAggregationService.fetchIssueAnalytics(authHeader, topLimit));
  }
}
