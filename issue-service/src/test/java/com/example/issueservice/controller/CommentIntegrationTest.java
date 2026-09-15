package com.example.issueservice.controller;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
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
 * Lifecycle for the Comment entity: POST(add) -> GET(list). Comment has no update/delete endpoint
 * by design (reporters and admins only ever add comments; there is no edit/delete requirement in
 * the business logic), so this test covers create + list/read only, which is this API's full
 * supported lifecycle for Comment.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CommentIntegrationTest {

  @Autowired private TestRestTemplate restTemplate;

  private static final RestTemplate USER_SERVICE_CLIENT = new RestTemplate();
  private static final String USER_SERVICE_BASE = "http://localhost:25169";

  private static String userToken;
  private static String commentText;

  private HttpHeaders authHeaders(String token) {
    HttpHeaders headers = new HttpHeaders();
    headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    return headers;
  }

  @Test
  @Order(1)
  void userCanAddCommentToExistingIssue() {
    Map<String, String> loginBody = Map.of("email", "dave@cityfix.com", "password", "Password123");
    ResponseEntity<Map> loginResp =
        USER_SERVICE_CLIENT.postForEntity(
            USER_SERVICE_BASE + "/api/v1/auth/login", loginBody, Map.class);
    userToken = (String) loginResp.getBody().get("token");

    commentText = "Integration test comment " + System.currentTimeMillis();
    Map<String, String> body = Map.of("text", commentText);

    ResponseEntity<Map> resp =
        restTemplate.exchange(
            "/api/v1/issues/1/comments",
            HttpMethod.POST,
            new HttpEntity<>(body, authHeaders(userToken)),
            Map.class);

    assertEquals(HttpStatus.OK, resp.getStatusCode());
    assertEquals(commentText, resp.getBody().get("text"));
    assertEquals("USER", resp.getBody().get("postedByRole"));
  }

  @Test
  @Order(2)
  void listComments_includesTheNewComment() {
    ResponseEntity<Map> resp =
        restTemplate.exchange(
            "/api/v1/issues/1/comments?size=100",
            HttpMethod.GET,
            new HttpEntity<>(authHeaders(userToken)),
            Map.class);

    assertEquals(HttpStatus.OK, resp.getStatusCode());
    List<Map<String, Object>> content = (List<Map<String, Object>>) resp.getBody().get("content");
    boolean found = content.stream().anyMatch(c -> commentText.equals(c.get("text")));
    assertTrue(found, "expected newly added comment to appear in the list");
  }

  @Test
  @Order(3)
  void addComment_toUnknownIssue_returns404() {
    Map<String, String> body = Map.of("text", "orphan comment");
    ResponseEntity<String> resp =
        restTemplate.exchange(
            "/api/v1/issues/999999/comments",
            HttpMethod.POST,
            new HttpEntity<>(body, authHeaders(userToken)),
            String.class);
    assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
  }
}
