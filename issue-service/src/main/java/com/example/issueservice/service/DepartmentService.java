package com.example.issueservice.service;

import com.example.issueservice.dto.DepartmentRequest;
import com.example.issueservice.dto.DepartmentResponse;
import com.example.issueservice.entity.Department;
import com.example.issueservice.exception.BadRequestException;
import com.example.issueservice.exception.ResourceNotFoundException;
import com.example.issueservice.repository.DepartmentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DepartmentService {

  private final DepartmentRepository departmentRepository;

  public DepartmentService(DepartmentRepository departmentRepository) {
    this.departmentRepository = departmentRepository;
  }

  public DepartmentResponse create(DepartmentRequest request) {
    if (departmentRepository.existsByName(request.getName())) {
      throw new BadRequestException("Department already exists: " + request.getName());
    }
    Department d = new Department();
    d.setName(request.getName());
    d.setDescription(request.getDescription());
    d.setCategoryHandled(request.getCategoryHandled());
    return DepartmentResponse.from(departmentRepository.save(d));
  }

  public DepartmentResponse update(Long id, DepartmentRequest request) {
    Department d =
        departmentRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Department not found: " + id));
    d.setName(request.getName());
    d.setDescription(request.getDescription());
    d.setCategoryHandled(request.getCategoryHandled());
    return DepartmentResponse.from(departmentRepository.save(d));
  }

  public void delete(Long id) {
    Department d =
        departmentRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Department not found: " + id));
    departmentRepository.delete(d);
  }

  public DepartmentResponse get(Long id) {
    Department d =
        departmentRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Department not found: " + id));
    return DepartmentResponse.from(d);
  }

  public Page<DepartmentResponse> list(Pageable pageable) {
    return departmentRepository.findAll(pageable).map(DepartmentResponse::from);
  }
}
