package com.example.issueservice.repository;

import com.example.issueservice.entity.IssueCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueCategoryRepository extends JpaRepository<IssueCategory, Long> {
  boolean existsByName(String name);
}
