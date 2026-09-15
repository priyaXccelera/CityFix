package com.example.adminannouncementservice.controller;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.nio.file.Files;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

/**
 * Inter-service integration test: admin-announcement-service's /api/v1/analytics endpoint uses a
 * real @LoadBalanced RestTemplate (see AnalyticsAggregationService) to call the already-running
 * issue-service (started in STEP 7) over Eureka discovery. issue-service is NOT mocked/stubbed
 * here.
 *
 * <p>grep -rlE "RestTemplate|WebClient" *&#47;src/main/java ->
 * admin-announcement-service/.../AdminAnnouncementServiceApplication.java (RestTemplate bean)
 * admin-announcement-service/.../service/AnalyticsAggregationService.java (the actual call site)
 * This is the ONLY cross-service call site in the whole system, so this is the only inter-service
 * integration test required.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AnalyticsIntegrationTest {

  @Autowired private TestRestTemplate restTemplate;

  private static final RestTemplate USER_SERVICE_CLIENT = new RestTemplate();
  private static final String USER_SERVICE_BASE = "http://localhost:25169";

  @Test
  void adminAnalytics_callsRealIssueServiceOverEureka_andCalleeLogConfirmsIt() throws Exception {
    Map<String, String> loginBody = Map.of("email", "admin@cityfix.com", "password", "Password123");
    ResponseEntity<Map> loginResp =
        USER_SERVICE_CLIENT.postForEntity(
            USER_SERVICE_BASE + "/api/v1/auth/login", loginBody, Map.class);
    String adminToken = (String) loginResp.getBody().get("token");

    HttpHeaders headers = new HttpHeaders();
    headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken);

    long before = System.currentTimeMillis();

    ResponseEntity<Map> resp =
        restTemplate.exchange(
            "/api/v1/analytics?topLimit=5", HttpMethod.GET, new HttpEntity<>(headers), Map.class);

    assertEquals(HttpStatus.OK, resp.getStatusCode());
    Map<String, Object> body = resp.getBody();
    assertNotNull(
        body.get("countsByStatus"), "caller response should carry issue-service's countsByStatus");
    assertNotNull(
        body.get("countsByCategory"),
        "caller response should carry issue-service's countsByCategory");
    assertTrue(body.containsKey("topUpvotedUnresolved"));

    // Give the async log append a brief moment, then confirm the callee actually
    // received the forwarded request (not a silent no-op / cached response).
    Thread.sleep(500);

    File issueServiceLog =
        new File(System.getProperty("user.dir"))
            .getParentFile()
            .toPath()
            .resolve("issue-service.log")
            .toFile();
    assertTrue(
        issueServiceLog.exists(),
        "expected issue-service.log at " + issueServiceLog.getAbsolutePath());

    String logContent = Files.readString(issueServiceLog.toPath());
    assertTrue(
        logContent.contains("INBOUND_ANALYTICS_REQUEST"),
        "issue-service.log has no record of an inbound analytics request; "
            + "caller response looked fine but callee never actually received the call");
  }
}
