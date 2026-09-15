package com.example.adminannouncementservice.controller;

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
 * Full CRUD lifecycle for the Announcement entity: POST -> GET -> GET/id -> PUT -> DELETE -> GET/id
 * (404).
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AnnouncementIntegrationTest {

  @Autowired private TestRestTemplate restTemplate;

  private static final RestTemplate USER_SERVICE_CLIENT = new RestTemplate();
  private static final String USER_SERVICE_BASE = "http://localhost:25169";

  private static String adminToken;
  private static String userToken;
  private static Long announcementId;

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
  void adminCanCreateAnnouncement() {
    adminToken = loginAndGetToken("admin@cityfix.com", "Password123");
    userToken = loginAndGetToken("bob@cityfix.com", "Password123");

    Map<String, String> body =
        Map.of("text", "Integration test announcement " + System.currentTimeMillis());
    ResponseEntity<Map> resp =
        restTemplate.exchange(
            "/api/v1/announcements",
            HttpMethod.POST,
            new HttpEntity<>(body, authHeaders(adminToken)),
            Map.class);

    assertEquals(HttpStatus.OK, resp.getStatusCode());
    announcementId = ((Number) resp.getBody().get("id")).longValue();
    assertEquals("Alice Admin", resp.getBody().get("postedByName"));
  }

  @Test
  @Order(2)
  void userCannotCreateAnnouncement_returns403() {
    Map<String, String> body = Map.of("text", "should fail");
    ResponseEntity<String> resp =
        restTemplate.exchange(
            "/api/v1/announcements",
            HttpMethod.POST,
            new HttpEntity<>(body, authHeaders(userToken)),
            String.class);
    assertEquals(HttpStatus.FORBIDDEN, resp.getStatusCode());
  }

  @Test
  @Order(3)
  void userCanListAnnouncements() {
    ResponseEntity<Map> resp =
        restTemplate.exchange(
            "/api/v1/announcements?size=50",
            HttpMethod.GET,
            new HttpEntity<>(authHeaders(userToken)),
            Map.class);
    assertEquals(HttpStatus.OK, resp.getStatusCode());
    assertTrue(((Number) resp.getBody().get("totalElements")).longValue() > 0);
  }

  @Test
  @Order(4)
  void getAnnouncementById_returnsCreatedAnnouncement() {
    ResponseEntity<Map> resp =
        restTemplate.exchange(
            "/api/v1/announcements/" + announcementId,
            HttpMethod.GET,
            new HttpEntity<>(authHeaders(userToken)),
            Map.class);
    assertEquals(HttpStatus.OK, resp.getStatusCode());
    assertEquals(announcementId.intValue(), ((Number) resp.getBody().get("id")).intValue());
  }

  @Test
  @Order(5)
  void adminCanUpdateAnnouncement() {
    Map<String, String> body = Map.of("text", "Updated text " + System.currentTimeMillis());
    ResponseEntity<Map> resp =
        restTemplate.exchange(
            "/api/v1/announcements/" + announcementId,
            HttpMethod.PUT,
            new HttpEntity<>(body, authHeaders(adminToken)),
            Map.class);
    assertEquals(HttpStatus.OK, resp.getStatusCode());
    assertTrue(((String) resp.getBody().get("text")).startsWith("Updated text"));
  }

  @Test
  @Order(6)
  void adminCanDeleteAnnouncement_andSubsequentGetReturns404() {
    ResponseEntity<Void> deleteResp =
        restTemplate.exchange(
            "/api/v1/announcements/" + announcementId,
            HttpMethod.DELETE,
            new HttpEntity<>(authHeaders(adminToken)),
            Void.class);
    assertEquals(HttpStatus.NO_CONTENT, deleteResp.getStatusCode());

    ResponseEntity<String> getResp =
        restTemplate.exchange(
            "/api/v1/announcements/" + announcementId,
            HttpMethod.GET,
            new HttpEntity<>(authHeaders(userToken)),
            String.class);
    assertEquals(HttpStatus.NOT_FOUND, getResp.getStatusCode());
  }
}
