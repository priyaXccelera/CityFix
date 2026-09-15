package com.example.issueservice.controller;

import com.example.issueservice.dto.CommentRequest;
import com.example.issueservice.dto.CommentResponse;
import com.example.issueservice.security.AuthenticatedUser;
import com.example.issueservice.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/issues/{issueId}/comments")
@Tag(name = "Comments", description = "Comments posted by reporters or admins on an issue")
public class CommentController {

  private final CommentService commentService;

  public CommentController(CommentService commentService) {
    this.commentService = commentService;
  }

  @PostMapping
  @Operation(summary = "Add a comment to an issue")
  public ResponseEntity<CommentResponse> add(
      @PathVariable Long issueId,
      @Valid @RequestBody CommentRequest request,
      @AuthenticationPrincipal AuthenticatedUser user) {
    return ResponseEntity.ok(commentService.add(issueId, request, user));
  }

  @GetMapping
  @Operation(summary = "List comments on an issue, paginated")
  public ResponseEntity<Page<CommentResponse>> list(
      @PathVariable Long issueId, @PageableDefault(size = 20) Pageable pageable) {
    return ResponseEntity.ok(commentService.list(issueId, pageable));
  }
}
