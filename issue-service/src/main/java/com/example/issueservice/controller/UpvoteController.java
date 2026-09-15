package com.example.issueservice.controller;

import com.example.issueservice.dto.UpvoteResponse;
import com.example.issueservice.security.AuthenticatedUser;
import com.example.issueservice.service.UpvoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/issues/{issueId}/upvotes")
@Tag(name = "Upvotes", description = "Upvote tracking for issues")
public class UpvoteController {

  private final UpvoteService upvoteService;

  public UpvoteController(UpvoteService upvoteService) {
    this.upvoteService = upvoteService;
  }

  @PostMapping
  @Operation(summary = "Upvote an issue")
  public ResponseEntity<UpvoteResponse> upvote(
      @PathVariable Long issueId, @AuthenticationPrincipal AuthenticatedUser user) {
    return ResponseEntity.ok(upvoteService.upvote(issueId, user.getUserId()));
  }
}
