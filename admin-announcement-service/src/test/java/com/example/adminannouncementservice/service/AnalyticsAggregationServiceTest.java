package com.example.adminannouncementservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class AnalyticsAggregationServiceTest {

  @Mock private RestTemplate restTemplate;

  @InjectMocks private AnalyticsAggregationService analyticsAggregationService;

  @Test
  void fetchIssueAnalytics_forwardsAuthorizationHeaderAndReturnsBody() {
    Map<String, Object> fakeBody = Map.of("countsByStatus", Map.of("REPORTED", 2));
    when(restTemplate.exchange(
            eq("http://issue-service/api/v1/issues/analytics?topLimit=5"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(Map.class)))
        .thenReturn(new ResponseEntity<>(fakeBody, org.springframework.http.HttpStatus.OK));

    Map<String, Object> result =
        analyticsAggregationService.fetchIssueAnalytics("Bearer sometoken", 5);

    assertEquals(fakeBody, result);
  }

  @Test
  void fetchIssueAnalytics_withNullAuthHeader_stillCallsIssueService() {
    Map<String, Object> fakeBody = Map.of("countsByStatus", Map.of());
    when(restTemplate.exchange(
            anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Map.class)))
        .thenReturn(new ResponseEntity<>(fakeBody, org.springframework.http.HttpStatus.OK));

    Map<String, Object> result = analyticsAggregationService.fetchIssueAnalytics(null, 3);

    assertNotNull(result);
    verify(restTemplate)
        .exchange(contains("topLimit=3"), eq(HttpMethod.GET), any(HttpEntity.class), eq(Map.class));
  }
}
