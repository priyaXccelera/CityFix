package com.example.adminannouncementservice.service;

import java.util.Map;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AnalyticsAggregationService {

  private final RestTemplate restTemplate;

  public AnalyticsAggregationService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  /**
   * Calls issue-service's analytics endpoint over Eureka discovery, forwarding the caller's
   * Authorization header so the ADMIN-only endpoint on issue-service authorizes the request.
   */
  @SuppressWarnings("unchecked")
  public Map<String, Object> fetchIssueAnalytics(String authorizationHeader, int topLimit) {
    HttpHeaders headers = new HttpHeaders();
    if (authorizationHeader != null) {
      headers.set(HttpHeaders.AUTHORIZATION, authorizationHeader);
    }
    HttpEntity<Void> entity = new HttpEntity<>(headers);

    String url = "http://issue-service/api/v1/issues/analytics?topLimit=" + topLimit;
    ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
    return (Map<String, Object>) response.getBody();
  }
}
