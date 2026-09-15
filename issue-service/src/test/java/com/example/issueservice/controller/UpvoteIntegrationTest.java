package com.example.issueservice.controller;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

/**
 * Lifecycle for the Upvote entity: POST(upvote) -> GET(issue reflects new count) -> duplicate POST
 * rejected. Upvote has no separate GET/list/DELETE endpoint by design (upvote counts are surfaced
 * on the Issue resource itself), so this is this API's full supported lifecycle for Upvote.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UpvoteIntegrationTest {

  @Autowired private TestRestTemplate restTemplate;

  private static final RestTemplate USER_SERVICE_CLIENT = new RestTemplate();
  private static final String USER_SERVICE_BASE = "http://localhost:25169";

  private static String userToken;
  private static long countBeforeUpvote;

  private HttpHeaders authHeaders(String token) {
    HttpHeaders headers = new HttpHeaders();
    headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    return headers;
  }

  @Test
  @Order(1)
  void userCanUpvoteIssue_incrementsCount() {
    Map<String, String> loginBody = Map.of("email", "dave@cityfix.com", "password", "Password123");
    ResponseEntity<Map> loginResp =
        USER_SERVICE_CLIENT.postForEntity(
            USER_SERVICE_BASE + "/api/v1/auth/login", loginBody, Map.class);
    userToken = (String) loginResp.getBody().get("token");

    ResponseEntity<Map> beforeResp =
        restTemplate.exchange(
            "/api/v1/issues/2",
            HttpMethod.GET,
            new HttpEntity<>(authHeaders(userToken)),
            Map.class);
    countBeforeUpvote = ((Number) beforeResp.getBody().get("upvoteCount")).longValue();

    ResponseEntity<Map> resp =
        restTemplate.exchange(
            "/api/v1/issues/2/upvotes",
            HttpMethod.POST,
            new HttpEntity<>(authHeaders(userToken)),
            Map.class);

    assertEquals(HttpStatus.OK, resp.getStatusCode());
    assertEquals(countBeforeUpvote + 1, ((Number) resp.getBody().get("upvoteCount")).longValue());
    assertEquals(true, resp.getBody().get("upvotedByCurrentUser"));
  }

  @Test
  @Order(2)
  void issueReflectsIncrementedUpvoteCount() {
    ResponseEntity<Map> resp =
        restTemplate.exchange(
            "/api/v1/issues/2",
            HttpMethod.GET,
            new HttpEntity<>(authHeaders(userToken)),
            Map.class);
    assertEquals(countBeforeUpvote + 1, ((Number) resp.getBody().get("upvoteCount")).longValue());
  }

  @Test
  @Order(3)
  void duplicateUpvote_isRejectedWith400() {
    ResponseEntity<String> resp =
        restTemplate.exchange(
            "/api/v1/issues/2/upvotes",
            HttpMethod.POST,
            new HttpEntity<>(authHeaders(userToken)),
            String.class);
    assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
  }
}
