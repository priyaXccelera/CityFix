package com.example.issueservice.controller;

import com.example.issueservice.dto.CategoryRequest;
import com.example.issueservice.dto.CategoryResponse;
import com.example.issueservice.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/categories")
@Tag(
    name = "Issue Categories",
    description = "Issue category management (create/edit/delete ADMIN only)")
public class CategoryController {

  private final CategoryService categoryService;

  public CategoryController(CategoryService categoryService) {
    this.categoryService = categoryService;
  }

  @PostMapping
  @Operation(summary = "Create an issue category (ADMIN only)")
  public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest request) {
    return ResponseEntity.ok(categoryService.create(request));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Edit an issue category (ADMIN only)")
  public ResponseEntity<CategoryResponse> update(
      @PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
    return ResponseEntity.ok(categoryService.update(id, request));
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete an issue category (ADMIN only)")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    categoryService.delete(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get an issue category by id")
  public ResponseEntity<CategoryResponse> get(@PathVariable Long id) {
    return ResponseEntity.ok(categoryService.get(id));
  }

  @GetMapping
  @Operation(summary = "List all issue categories, paginated")
  public ResponseEntity<Page<CategoryResponse>> list(
      @PageableDefault(size = 20) Pageable pageable) {
    return ResponseEntity.ok(categoryService.list(pageable));
  }
}
