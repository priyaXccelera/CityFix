package com.example.issueservice.service;

import com.example.issueservice.dto.CategoryRequest;
import com.example.issueservice.dto.CategoryResponse;
import com.example.issueservice.entity.Department;
import com.example.issueservice.entity.IssueCategory;
import com.example.issueservice.exception.BadRequestException;
import com.example.issueservice.exception.ResourceNotFoundException;
import com.example.issueservice.repository.DepartmentRepository;
import com.example.issueservice.repository.IssueCategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

  private final IssueCategoryRepository categoryRepository;
  private final DepartmentRepository departmentRepository;

  public CategoryService(
      IssueCategoryRepository categoryRepository, DepartmentRepository departmentRepository) {
    this.categoryRepository = categoryRepository;
    this.departmentRepository = departmentRepository;
  }

  public CategoryResponse create(CategoryRequest request) {
    if (categoryRepository.existsByName(request.getName())) {
      throw new BadRequestException("Category already exists: " + request.getName());
    }
    Department department =
        departmentRepository
            .findById(request.getDepartmentId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Department not found: " + request.getDepartmentId()));

    IssueCategory c = new IssueCategory();
    c.setName(request.getName());
    c.setDepartment(department);
    c.setDefaultPriority(request.getDefaultPriority());
    return CategoryResponse.from(categoryRepository.save(c));
  }

  public CategoryResponse update(Long id, CategoryRequest request) {
    IssueCategory c =
        categoryRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
    Department department =
        departmentRepository
            .findById(request.getDepartmentId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Department not found: " + request.getDepartmentId()));
    c.setName(request.getName());
    c.setDepartment(department);
    c.setDefaultPriority(request.getDefaultPriority());
    return CategoryResponse.from(categoryRepository.save(c));
  }

  public void delete(Long id) {
    IssueCategory c =
        categoryRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
    categoryRepository.delete(c);
  }

  public CategoryResponse get(Long id) {
    return CategoryResponse.from(
        categoryRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id)));
  }

  public Page<CategoryResponse> list(Pageable pageable) {
    return categoryRepository.findAll(pageable).map(CategoryResponse::from);
  }
}
