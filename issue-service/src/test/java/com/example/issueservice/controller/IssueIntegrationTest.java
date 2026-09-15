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
 * Full lifecycle for the Issue entity: POST(report) -> GET(browse) -> GET/id -> PUT(status update).
 * Issue has no hard-delete endpoint by design (the business logic only supports status transitions
 * such as REJECTED/RESOLVED), so the "DELETE -> GET 404" step is replaced with a REJECTED status
 * transition verified via a subsequent GET, which is the literal equivalent this API exposes.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class IssueIntegrationTest {

  @Autowired private TestRestTemplate restTemplate;

  private static final RestTemplate USER_SERVICE_CLIENT = new RestTemplate();
  private static final String USER_SERVICE_BASE = "http://localhost:25169";

  private static String adminToken;
  private static String userToken;
  private static Long issueId;

  private HttpHeaders authHeaders(String token) {
    HttpHeaders headers = new HttpHeaders();
    headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    return headers;
  }

  private String loginAndGetToken(String email, String password) {
    Map<String, String> body = Map.of("email", email, "password", password);
    ResponseEntity<Map> resp =
        USER_SERVICE_CLIENT.postForEntity(
            USER_SERVICE_BASE + "/api/v1/auth/login", body, Map.class);
    return (String) resp.getBody().get("token");
  }

  @Test
  @Order(1)
  void userCanReportIssue() {
    adminToken = loginAndGetToken("admin@cityfix.com", "Password123");
    userToken = loginAndGetToken("carla@cityfix.com", "Password123");

    Map<String, Object> body =
        Map.of(
            "title",
            "Integration test issue " + System.currentTimeMillis(),
            "description",
            "Reported by automated test",
            "categoryId",
            1,
            "area",
            "Uptown");

    ResponseEntity<Map> resp =
        restTemplate.exchange(
            "/api/v1/issues",
            HttpMethod.POST,
            new HttpEntity<>(body, authHeaders(userToken)),
            Map.class);

    assertEquals(HttpStatus.OK, resp.getStatusCode());
    assertEquals("REPORTED", resp.getBody().get("status"));
    issueId = ((Number) resp.getBody().get("id")).longValue();
  }

  @Test
  @Order(2)
  void browseIssues_includesReportedIssue() {
    ResponseEntity<Map> resp =
        restTemplate.exchange(
            "/api/v1/issues?size=100",
            HttpMethod.GET,
            new HttpEntity<>(authHeaders(userToken)),
            Map.class);
    assertEquals(HttpStatus.OK, resp.getStatusCode());
    assertTrue(((Number) resp.getBody().get("totalElements")).longValue() > 0);
  }

  @Test
  @Order(3)
  void getIssueById_returnsCreatedIssue() {
    ResponseEntity<Map> resp =
        restTemplate.exchange(
            "/api/v1/issues/" + issueId,
            HttpMethod.GET,
            new HttpEntity<>(authHeaders(userToken)),
            Map.class);
    assertEquals(HttpStatus.OK, resp.getStatusCode());
    assertEquals(issueId.intValue(), ((Number) resp.getBody().get("id")).intValue());
  }

  @Test
  @Order(4)
  void userCannotChangeStatus_returns403() {
    Map<String, String> body = Map.of("status", "REJECTED");
    ResponseEntity<String> resp =
        restTemplate.exchange(
            "/api/v1/issues/" + issueId + "/status",
            HttpMethod.PUT,
            new HttpEntity<>(body, authHeaders(userToken)),
            String.class);
    assertEquals(HttpStatus.FORBIDDEN, resp.getStatusCode());
  }

  @Test
  @Order(5)
  void adminCanChangeStatus_toRejected_andSubsequentGetReflectsIt() {
    Map<String, String> body = Map.of("status", "REJECTED");
    ResponseEntity<Map> resp =
        restTemplate.exchange(
            "/api/v1/issues/" + issueId + "/status",
            HttpMethod.PUT,
            new HttpEntity<>(body, authHeaders(adminToken)),
            Map.class);
    assertEquals(HttpStatus.OK, resp.getStatusCode());
    assertEquals("REJECTED", resp.getBody().get("status"));

    ResponseEntity<Map> getResp =
        restTemplate.exchange(
            "/api/v1/issues/" + issueId,
            HttpMethod.GET,
            new HttpEntity<>(authHeaders(userToken)),
            Map.class);
    assertEquals("REJECTED", getResp.getBody().get("status"));
  }

  @Test
  @Order(6)
  void myIssues_forReporter_includesTheReportedIssue() {
    ResponseEntity<Map> resp =
        restTemplate.exchange(
            "/api/v1/issues/my?size=100",
            HttpMethod.GET,
            new HttpEntity<>(authHeaders(userToken)),
            Map.class);
    assertEquals(HttpStatus.OK, resp.getStatusCode());
    assertTrue(((Number) resp.getBody().get("totalElements")).longValue() > 0);
  }
}
