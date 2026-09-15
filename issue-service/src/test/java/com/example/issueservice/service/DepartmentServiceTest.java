package com.example.issueservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.issueservice.dto.DepartmentRequest;
import com.example.issueservice.dto.DepartmentResponse;
import com.example.issueservice.entity.Department;
import com.example.issueservice.exception.BadRequestException;
import com.example.issueservice.exception.ResourceNotFoundException;
import com.example.issueservice.repository.DepartmentRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

  @Mock private DepartmentRepository departmentRepository;

  @InjectMocks private DepartmentService departmentService;

  @Test
  void create_withDuplicateName_throwsBadRequest() {
    DepartmentRequest req = new DepartmentRequest();
    req.setName("Public Works");

    when(departmentRepository.existsByName("Public Works")).thenReturn(true);

    assertThrows(BadRequestException.class, () -> departmentService.create(req));
    verify(departmentRepository, never()).save(any());
  }

  @Test
  void create_withValidData_savesAndReturnsResponse() {
    DepartmentRequest req = new DepartmentRequest();
    req.setName("Sanitation");
    req.setDescription("Waste management");
    req.setCategoryHandled("Waste");

    when(departmentRepository.existsByName("Sanitation")).thenReturn(false);
    when(departmentRepository.save(any(Department.class)))
        .thenAnswer(
            inv -> {
              Department d = inv.getArgument(0);
              d.setId(10L);
              return d;
            });

    DepartmentResponse response = departmentService.create(req);

    assertEquals(10L, response.getId());
    assertEquals("Sanitation", response.getName());
  }

  @Test
  void get_notFound_throwsResourceNotFound() {
    when(departmentRepository.findById(999L)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> departmentService.get(999L));
  }

  @Test
  void update_existing_updatesFields() {
    Department existing = new Department();
    existing.setId(1L);
    existing.setName("Old Name");

    DepartmentRequest req = new DepartmentRequest();
    req.setName("New Name");
    req.setDescription("Updated description");
    req.setCategoryHandled("Roads");

    when(departmentRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(departmentRepository.save(any(Department.class))).thenAnswer(inv -> inv.getArgument(0));

    DepartmentResponse response = departmentService.update(1L, req);

    assertEquals("New Name", response.getName());
    assertEquals("Updated description", response.getDescription());
  }
}
