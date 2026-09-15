package com.example.adminannouncementservice.controller;

import com.example.adminannouncementservice.dto.AnnouncementRequest;
import com.example.adminannouncementservice.dto.AnnouncementResponse;
import com.example.adminannouncementservice.security.AuthenticatedUser;
import com.example.adminannouncementservice.service.AnnouncementService;
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
@RequestMapping("/api/v1/announcements")
@Tag(name = "Announcements", description = "Official CityFix announcements posted by admins")
public class AnnouncementController {

  private final AnnouncementService announcementService;

  public AnnouncementController(AnnouncementService announcementService) {
    this.announcementService = announcementService;
  }

  @PostMapping
  @Operation(summary = "Post a new announcement (ADMIN only)")
  public ResponseEntity<AnnouncementResponse> create(
      @Valid @RequestBody AnnouncementRequest request,
      @AuthenticationPrincipal AuthenticatedUser user) {
    return ResponseEntity.ok(announcementService.create(request, user));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Edit an announcement (ADMIN only)")
  public ResponseEntity<AnnouncementResponse> update(
      @PathVariable Long id, @Valid @RequestBody AnnouncementRequest request) {
    return ResponseEntity.ok(announcementService.update(id, request));
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete an announcement (ADMIN only)")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    announcementService.delete(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get an announcement by id")
  public ResponseEntity<AnnouncementResponse> get(@PathVariable Long id) {
    return ResponseEntity.ok(announcementService.get(id));
  }

  @GetMapping
  @Operation(summary = "View official announcements, paginated")
  public ResponseEntity<Page<AnnouncementResponse>> list(
      @PageableDefault(size = 20) Pageable pageable) {
    return ResponseEntity.ok(announcementService.list(pageable));
  }
}
