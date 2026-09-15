package com.example.issueservice.controller;

import com.example.issueservice.dto.DepartmentRequest;
import com.example.issueservice.dto.DepartmentResponse;
import com.example.issueservice.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/departments")
@Tag(name = "Departments", description = "Department management (create/edit/delete ADMIN only)")
public class DepartmentController {

  private final DepartmentService departmentService;

  public DepartmentController(DepartmentService departmentService) {
    this.departmentService = departmentService;
  }

  @PostMapping
  @Operation(summary = "Create a department (ADMIN only)")
  public ResponseEntity<DepartmentResponse> create(@Valid @RequestBody DepartmentRequest request) {
    return ResponseEntity.ok(departmentService.create(request));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Edit a department (ADMIN only)")
  public ResponseEntity<DepartmentResponse> update(
      @PathVariable Long id, @Valid @RequestBody DepartmentRequest request) {
    return ResponseEntity.ok(departmentService.update(id, request));
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete a department (ADMIN only)")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    departmentService.delete(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get a department by id")
  public ResponseEntity<DepartmentResponse> get(@PathVariable Long id) {
    return ResponseEntity.ok(departmentService.get(id));
  }

  @GetMapping
  @Operation(summary = "List all departments, paginated")
  public ResponseEntity<Page<DepartmentResponse>> list(
      @PageableDefault(size = 20) Pageable pageable) {
    return ResponseEntity.ok(departmentService.list(pageable));
  }
}
