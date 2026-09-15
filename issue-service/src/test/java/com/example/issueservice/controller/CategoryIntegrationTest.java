package com.example.issueservice.controller;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

/**
 * Full CRUD lifecycle for the IssueCategory entity: POST -> GET -> GET/id -> PUT -> DELETE ->
 * GET/id (404).
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CategoryIntegrationTest {

  @Autowired private TestRestTemplate restTemplate;

  private static final RestTemplate USER_SERVICE_CLIENT = new RestTemplate();
  private static final String USER_SERVICE_BASE = "http://localhost:25169";

  private static String adminToken;
  private static Long setupDepartmentId;
  private static Long categoryId;

  private HttpHeaders authHeaders(String token) {
    HttpHeaders headers = new HttpHeaders();
    headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    return headers;
  }

  @BeforeAll
  static void setup(@Autowired TestRestTemplate restTemplate) {
    Map<String, String> body = Map.of("email", "admin@cityfix.com", "password", "Password123");
    ResponseEntity<Map> resp =
        USER_SERVICE_CLIENT.postForEntity(
            USER_SERVICE_BASE + "/api/v1/auth/login", body, Map.class);
    adminToken = (String) resp.getBody().get("token");

    HttpHeaders headers = new HttpHeaders();
    headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken);
    Map<String, String> deptBody =
        Map.of("name", "CategoryTest_Dept_" + System.currentTimeMillis());
    ResponseEntity<Map> deptResp =
        restTemplate.exchange(
            "/api/v1/departments", HttpMethod.POST, new HttpEntity<>(deptBody, headers), Map.class);
    setupDepartmentId = ((Number) deptResp.getBody().get("id")).longValue();
  }

  @AfterAll
  static void cleanup(@Autowired TestRestTemplate restTemplate) {
    HttpHeaders headers = new HttpHeaders();
    headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken);
    if (setupDepartmentId != null) {
      restTemplate.exchange(
          "/api/v1/departments/" + setupDepartmentId,
          HttpMethod.DELETE,
          new HttpEntity<>(headers),
          Void.class);
    }
  }

  @Test
  @Order(1)
  void adminCanCreateCategory() {
    Map<String, Object> body =
        Map.of(
            "name",
            "TestCategory_" + System.currentTimeMillis(),
            "departmentId",
            setupDepartmentId,
            "defaultPriority",
            "MEDIUM");

    ResponseEntity<Map> resp =
        restTemplate.exchange(
            "/api/v1/categories",
            HttpMethod.POST,
            new HttpEntity<>(body, authHeaders(adminToken)),
            Map.class);

    assertEquals(HttpStatus.OK, resp.getStatusCode());
    categoryId = ((Number) resp.getBody().get("id")).longValue();
    assertEquals("MEDIUM", resp.getBody().get("defaultPriority"));
  }

  @Test
  @Order(2)
  void listCategories_returnsPagedResult() {
    ResponseEntity<Map> resp =
        restTemplate.exchange(
            "/api/v1/categories?size=50",
            HttpMethod.GET,
            new HttpEntity<>(authHeaders(adminToken)),
            Map.class);
    assertEquals(HttpStatus.OK, resp.getStatusCode());
    assertTrue(((Number) resp.getBody().get("totalElements")).longValue() > 0);
  }

  @Test
  @Order(3)
  void getCategoryById_returnsCreatedCategory() {
    ResponseEntity<Map> resp =
        restTemplate.exchange(
            "/api/v1/categories/" + categoryId,
            HttpMethod.GET,
            new HttpEntity<>(authHeaders(adminToken)),
            Map.class);
    assertEquals(HttpStatus.OK, resp.getStatusCode());
    assertEquals(categoryId.intValue(), ((Number) resp.getBody().get("id")).intValue());
  }

  @Test
  @Order(4)
  void adminCanUpdateCategory() {
    Map<String, Object> body =
        Map.of(
            "name",
            "TestCategory_Updated_" + System.currentTimeMillis(),
            "departmentId",
            setupDepartmentId,
            "defaultPriority",
            "URGENT");
    ResponseEntity<Map> resp =
        restTemplate.exchange(
            "/api/v1/categories/" + categoryId,
            HttpMethod.PUT,
            new HttpEntity<>(body, authHeaders(adminToken)),
            Map.class);
    assertEquals(HttpStatus.OK, resp.getStatusCode());
    assertEquals("URGENT", resp.getBody().get("defaultPriority"));
  }

  @Test
  @Order(5)
  void adminCanDeleteCategory_andSubsequentGetReturns404() {
    ResponseEntity<Void> deleteResp =
        restTemplate.exchange(
            "/api/v1/categories/" + categoryId,
            HttpMethod.DELETE,
            new HttpEntity<>(authHeaders(adminToken)),
            Void.class);
    assertEquals(HttpStatus.NO_CONTENT, deleteResp.getStatusCode());

    ResponseEntity<String> getResp =
        restTemplate.exchange(
            "/api/v1/categories/" + categoryId,
            HttpMethod.GET,
            new HttpEntity<>(authHeaders(adminToken)),
            String.class);
    assertEquals(HttpStatus.NOT_FOUND, getResp.getStatusCode());
  }
}
