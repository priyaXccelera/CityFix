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
 * Full CRUD lifecycle for the Department entity: POST -> GET -> GET/id -> PUT -> DELETE -> GET/id
 * (404). Tokens are obtained from the real, already-running user-service (STEP 7) via
 * /api/v1/auth/**.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DepartmentIntegrationTest {

  @Autowired private TestRestTemplate restTemplate;

  private static final RestTemplate USER_SERVICE_CLIENT = new RestTemplate();
  private static final String USER_SERVICE_BASE = "http://localhost:25169";

  private static String adminToken;
  private static String userToken;
  private static Long departmentId;

  private String loginAndGetToken(String email, String password) {
    Map<String, String> body = Map.of("email", email, "password", password);
    ResponseEntity<Map> resp =
        USER_SERVICE_CLIENT.postForEntity(
            USER_SERVICE_BASE + "/api/v1/auth/login", body, Map.class);
    assertEquals(HttpStatus.OK, resp.getStatusCode());
    return (String) resp.getBody().get("token");
  }

  private HttpHeaders authHeaders(String token) {
    HttpHeaders headers = new HttpHeaders();
    headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    return headers;
  }

  @Test
  @Order(1)
  void adminCanCreateDepartment() {
    adminToken = loginAndGetToken("admin@cityfix.com", "Password123");
    userToken = loginAndGetToken("bob@cityfix.com", "Password123");

    Map<String, String> body =
        Map.of(
            "name",
            "IT_Dept_" + System.currentTimeMillis(),
            "description",
            "IT infrastructure issues",
            "categoryHandled",
            "IT");

    ResponseEntity<Map> resp =
        restTemplate.exchange(
            "/api/v1/departments",
            HttpMethod.POST,
            new HttpEntity<>(body, authHeaders(adminToken)),
            Map.class);

    assertEquals(HttpStatus.OK, resp.getStatusCode());
    assertNotNull(resp.getBody().get("id"));
    departmentId = ((Number) resp.getBody().get("id")).longValue();
  }

  @Test
  @Order(2)
  void userRoleCannotCreateDepartment_returns403() {
    Map<String, String> body = Map.of("name", "Should Fail");
    ResponseEntity<String> resp =
        restTemplate.exchange(
            "/api/v1/departments",
            HttpMethod.POST,
            new HttpEntity<>(body, authHeaders(userToken)),
            String.class);
    assertEquals(HttpStatus.FORBIDDEN, resp.getStatusCode());
  }

  @Test
  @Order(3)
  void listDepartments_returnsPagedResult() {
    ResponseEntity<Map> resp =
        restTemplate.exchange(
            "/api/v1/departments?size=50",
            HttpMethod.GET,
            new HttpEntity<>(authHeaders(userToken)),
            Map.class);
    assertEquals(HttpStatus.OK, resp.getStatusCode());
    assertTrue(((Number) resp.getBody().get("totalElements")).longValue() > 0);
  }

  @Test
  @Order(4)
  void getDepartmentById_returnsCreatedDepartment() {
    ResponseEntity<Map> resp =
        restTemplate.exchange(
            "/api/v1/departments/" + departmentId,
            HttpMethod.GET,
            new HttpEntity<>(authHeaders(userToken)),
            Map.class);
    assertEquals(HttpStatus.OK, resp.getStatusCode());
    assertEquals(departmentId.intValue(), ((Number) resp.getBody().get("id")).intValue());
  }

  @Test
  @Order(5)
  void adminCanUpdateDepartment() {
    Map<String, String> body =
        Map.of(
            "name",
            "IT_Dept_Updated_" + System.currentTimeMillis(),
            "description",
            "Updated description",
            "categoryHandled",
            "IT");
    ResponseEntity<Map> resp =
        restTemplate.exchange(
            "/api/v1/departments/" + departmentId,
            HttpMethod.PUT,
            new HttpEntity<>(body, authHeaders(adminToken)),
            Map.class);
    assertEquals(HttpStatus.OK, resp.getStatusCode());
    assertEquals("Updated description", resp.getBody().get("description"));
  }

  @Test
  @Order(6)
  void adminCanDeleteDepartment_andSubsequentGetReturns404() {
    ResponseEntity<Void> deleteResp =
        restTemplate.exchange(
            "/api/v1/departments/" + departmentId,
            HttpMethod.DELETE,
            new HttpEntity<>(authHeaders(adminToken)),
            Void.class);
    assertEquals(HttpStatus.NO_CONTENT, deleteResp.getStatusCode());

    ResponseEntity<String> getResp =
        restTemplate.exchange(
            "/api/v1/departments/" + departmentId,
            HttpMethod.GET,
            new HttpEntity<>(authHeaders(userToken)),
            String.class);
    assertEquals(HttpStatus.NOT_FOUND, getResp.getStatusCode());
  }
}
