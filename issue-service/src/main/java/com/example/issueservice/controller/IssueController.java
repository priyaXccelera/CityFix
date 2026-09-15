package com.example.issueservice.controller;

import com.example.issueservice.dto.*;
import com.example.issueservice.entity.IssueStatus;
import com.example.issueservice.entity.Priority;
import com.example.issueservice.security.AuthenticatedUser;
import com.example.issueservice.service.IssueService;
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
@RequestMapping("/api/v1/issues")
@Tag(name = "Issues", description = "Report, browse, and manage civic issues")
public class IssueController {

  private final IssueService issueService;

  public IssueController(IssueService issueService) {
    this.issueService = issueService;
  }

  @PostMapping
  @Operation(summary = "Report a new issue")
  public ResponseEntity<IssueResponse> report(
      @Valid @RequestBody IssueRequest request, @AuthenticationPrincipal AuthenticatedUser user) {
    return ResponseEntity.ok(issueService.report(request, user));
  }

  @GetMapping
  @Operation(
      summary =
          "Browse public issues, paginated, optionally filtered by"
              + " department/category/status/priority/area")
  public ResponseEntity<Page<IssueResponse>> browse(
      @RequestParam(required = false) Long departmentId,
      @RequestParam(required = false) Long categoryId,
      @RequestParam(required = false) IssueStatus status,
      @RequestParam(required = false) Priority priority,
      @RequestParam(required = false) String area,
      @PageableDefault(size = 20) Pageable pageable) {
    return ResponseEntity.ok(
        issueService.browse(departmentId, categoryId, status, priority, area, pageable));
  }

  @GetMapping("/my")
  @Operation(summary = "View my own reported issues and their status history")
  public ResponseEntity<Page<IssueResponse>> myIssues(
      @AuthenticationPrincipal AuthenticatedUser user,
      @PageableDefault(size = 20) Pageable pageable) {
    return ResponseEntity.ok(issueService.myIssues(user.getUserId(), pageable));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get an issue by id")
  public ResponseEntity<IssueResponse> get(@PathVariable Long id) {
    return ResponseEntity.ok(issueService.get(id));
  }

  @PutMapping("/{id}/assign")
  @Operation(summary = "Assign an issue to a department (ADMIN only)")
  public ResponseEntity<IssueResponse> assign(
      @PathVariable Long id, @Valid @RequestBody AssignDepartmentRequest request) {
    return ResponseEntity.ok(issueService.assignDepartment(id, request.getDepartmentId()));
  }

  @PutMapping("/{id}/status")
  @Operation(summary = "Change an issue's status (ADMIN only)")
  public ResponseEntity<IssueResponse> updateStatus(
      @PathVariable Long id, @Valid @RequestBody UpdateStatusRequest request) {
    return ResponseEntity.ok(issueService.updateStatus(id, request.getStatus()));
  }

  @PutMapping("/{id}/priority")
  @Operation(summary = "Set/update an issue's priority (ADMIN only)")
  public ResponseEntity<IssueResponse> updatePriority(
      @PathVariable Long id, @Valid @RequestBody UpdatePriorityRequest request) {
    return ResponseEntity.ok(issueService.updatePriority(id, request.getPriority()));
  }
}
