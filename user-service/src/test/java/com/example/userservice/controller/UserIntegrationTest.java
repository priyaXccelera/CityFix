package com.example.userservice.controller;

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

/**
 * Full lifecycle test for the User entity, exercised through the real /api/v1/auth/** and
 * /api/v1/users/** endpoints (no mocks, real DB).
 *
 * <p>User has no hard-delete endpoint by design (only ADMIN "deactivate"), so the CRUD lifecycle
 * is: POST(register) -> GET(list) -> GET/id -> PUT(deactivate) -> GET/id again, asserting the
 * deactivated state instead of a 404.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserIntegrationTest {

  @Autowired private TestRestTemplate restTemplate;

  private static String newUserEmail;
  private static Long newUserId;
  private static String adminToken;

  private String uniqueEmail() {
    return "itest_" + System.currentTimeMillis() + "@cityfix.com";
  }

  private String loginAndGetToken(String email, String password) {
    Map<String, String> body = Map.of("email", email, "password", password);
    ResponseEntity<Map> resp = restTemplate.postForEntity("/api/v1/auth/login", body, Map.class);
    assertEquals(HttpStatus.OK, resp.getStatusCode());
    return (String) resp.getBody().get("token");
  }

  @Test
  @Order(1)
  void register_createsNewUser() {
    newUserEmail = uniqueEmail();
    Map<String, String> body =
        Map.of(
            "name", "Integration Tester",
            "email", newUserEmail,
            "password", "Password123",
            "address", "Downtown",
            "phone", "555-1234");
    ResponseEntity<Map> resp = restTemplate.postForEntity("/api/v1/auth/register", body, Map.class);

    assertEquals(HttpStatus.OK, resp.getStatusCode());
    Map<?, ?> respBody = resp.getBody();
    assertNotNull(respBody.get("token"));
    Map<?, ?> user = (Map<?, ?>) respBody.get("user");
    assertEquals(newUserEmail, user.get("email"));
    assertEquals("USER", user.get("role"));
    newUserId = ((Number) user.get("id")).longValue();
  }

  @Test
  @Order(2)
  void adminCanListUsers() {
    adminToken = loginAndGetToken("admin@cityfix.com", "Password123");

    HttpHeaders headers = new HttpHeaders();
    headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken);
    ResponseEntity<Map> resp =
        restTemplate.exchange(
            "/api/v1/users?size=50", HttpMethod.GET, new HttpEntity<>(headers), Map.class);

    assertEquals(HttpStatus.OK, resp.getStatusCode());
    assertTrue(((Number) resp.getBody().get("totalElements")).longValue() > 0);
  }

  @Test
  @Order(3)
  void userRoleCannotListUsers_returns403() {
    String userToken = loginAndGetToken(newUserEmail, "Password123");

    HttpHeaders headers = new HttpHeaders();
    headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + userToken);
    ResponseEntity<String> resp =
        restTemplate.exchange(
            "/api/v1/users", HttpMethod.GET, new HttpEntity<>(headers), String.class);

    assertEquals(HttpStatus.FORBIDDEN, resp.getStatusCode());
  }

  @Test
  @Order(4)
  void adminCanGetUserById() {
    HttpHeaders headers = new HttpHeaders();
    headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken);
    ResponseEntity<Map> resp =
        restTemplate.exchange(
            "/api/v1/users/" + newUserId, HttpMethod.GET, new HttpEntity<>(headers), Map.class);

    assertEquals(HttpStatus.OK, resp.getStatusCode());
    assertEquals(newUserEmail, resp.getBody().get("email"));
    assertEquals(true, resp.getBody().get("active"));
  }

  @Test
  @Order(5)
  void adminCanDeactivateUser_andSubsequentGetReflectsInactive() {
    HttpHeaders headers = new HttpHeaders();
    headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken);

    ResponseEntity<Map> deactivateResp =
        restTemplate.exchange(
            "/api/v1/users/" + newUserId + "/deactivate",
            HttpMethod.PUT,
            new HttpEntity<>(headers),
            Map.class);
    assertEquals(HttpStatus.OK, deactivateResp.getStatusCode());
    assertEquals(false, deactivateResp.getBody().get("active"));

    ResponseEntity<Map> getResp =
        restTemplate.exchange(
            "/api/v1/users/" + newUserId, HttpMethod.GET, new HttpEntity<>(headers), Map.class);
    assertEquals(HttpStatus.OK, getResp.getStatusCode());
    assertEquals(false, getResp.getBody().get("active"));
  }

  @Test
  @Order(6)
  void deactivatedUserCannotLoginAnymore() {
    Map<String, String> body = Map.of("email", newUserEmail, "password", "Password123");
    ResponseEntity<Map> resp = restTemplate.postForEntity("/api/v1/auth/login", body, Map.class);
    assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
  }
}
