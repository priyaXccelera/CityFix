package com.example.issueservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.issueservice.dto.CategoryRequest;
import com.example.issueservice.dto.CategoryResponse;
import com.example.issueservice.entity.Department;
import com.example.issueservice.entity.IssueCategory;
import com.example.issueservice.entity.Priority;
import com.example.issueservice.exception.BadRequestException;
import com.example.issueservice.exception.ResourceNotFoundException;
import com.example.issueservice.repository.DepartmentRepository;
import com.example.issueservice.repository.IssueCategoryRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

  @Mock private IssueCategoryRepository categoryRepository;

  @Mock private DepartmentRepository departmentRepository;

  @InjectMocks private CategoryService categoryService;

  @Test
  void create_withUnknownDepartment_throwsResourceNotFound() {
    CategoryRequest req = new CategoryRequest();
    req.setName("Pothole");
    req.setDepartmentId(99L);
    req.setDefaultPriority(Priority.HIGH);

    when(categoryRepository.existsByName("Pothole")).thenReturn(false);
    when(departmentRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> categoryService.create(req));
  }

  @Test
  void create_withDuplicateName_throwsBadRequest() {
    CategoryRequest req = new CategoryRequest();
    req.setName("Pothole");
    req.setDepartmentId(1L);
    req.setDefaultPriority(Priority.HIGH);

    when(categoryRepository.existsByName("Pothole")).thenReturn(true);

    assertThrows(BadRequestException.class, () -> categoryService.create(req));
    verify(departmentRepository, never()).findById(any());
  }

  @Test
  void create_withValidData_savesAndReturnsResponseWithDefaultPriority() {
    CategoryRequest req = new CategoryRequest();
    req.setName("Streetlight");
    req.setDepartmentId(1L);
    req.setDefaultPriority(Priority.LOW);

    Department dept = new Department();
    dept.setId(1L);
    dept.setName("Public Works");

    when(categoryRepository.existsByName("Streetlight")).thenReturn(false);
    when(departmentRepository.findById(1L)).thenReturn(Optional.of(dept));
    when(categoryRepository.save(any(IssueCategory.class)))
        .thenAnswer(
            inv -> {
              IssueCategory c = inv.getArgument(0);
              c.setId(7L);
              return c;
            });

    CategoryResponse response = categoryService.create(req);

    assertEquals(7L, response.getId());
    assertEquals(Priority.LOW, response.getDefaultPriority());
    assertEquals("Public Works", response.getDepartmentName());
  }

  @Test
  void get_notFound_throwsResourceNotFound() {
    when(categoryRepository.findById(5L)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> categoryService.get(5L));
  }
}
