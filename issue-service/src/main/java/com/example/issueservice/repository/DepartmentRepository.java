package com.example.issueservice.repository;

import com.example.issueservice.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
  boolean existsByName(String name);
}
