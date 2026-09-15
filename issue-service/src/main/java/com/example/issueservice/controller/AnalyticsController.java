package com.example.issueservice.controller;

import com.example.issueservice.dto.AnalyticsResponse;
import com.example.issueservice.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/issues/analytics")
@Tag(
    name = "Issue Analytics",
    description =
        "Admin-only analytics over issues (status/category counts, resolution time, top upvoted)")
public class AnalyticsController {

  private static final Logger log = LoggerFactory.getLogger(AnalyticsController.class);

  private final AnalyticsService analyticsService;

  public AnalyticsController(AnalyticsService analyticsService) {
    this.analyticsService = analyticsService;
  }

  @GetMapping
  @Operation(
      summary =
          "Aggregated issue analytics: counts by status/category, average resolution time, most"
              + " upvoted unresolved issues (ADMIN only)")
  public ResponseEntity<AnalyticsResponse> analytics(
      @RequestParam(defaultValue = "5") int topLimit) {
    log.info(
        "INBOUND_ANALYTICS_REQUEST topLimit={} timestamp={}", topLimit, System.currentTimeMillis());
    return ResponseEntity.ok(analyticsService.buildAnalytics(topLimit));
  }
}
