package com.example.issueservice.repository;

import com.example.issueservice.entity.Issue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface IssueRepository
    extends JpaRepository<Issue, Long>, JpaSpecificationExecutor<Issue> {

  Page<Issue> findByReportedByUserId(Long reportedByUserId, Pageable pageable);

  @Query("select i.status as status, count(i) as cnt from Issue i group by i.status")
  java.util.List<Object[]> countGroupedByStatus();

  @Query(
      "select i.category.name as categoryName, count(i) as cnt from Issue i group by"
          + " i.category.name")
  java.util.List<Object[]> countGroupedByCategory();

  @Query(
      value =
          "select avg(timestampdiff(hour, created_at, resolved_at)) from issues "
              + "where status = 'RESOLVED' and resolved_at is not null",
      nativeQuery = true)
  Double averageResolutionHours();
}
