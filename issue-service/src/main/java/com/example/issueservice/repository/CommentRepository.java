package com.example.issueservice.repository;

import com.example.issueservice.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
  Page<Comment> findByIssueId(Long issueId, Pageable pageable);

  long countByIssueId(Long issueId);
}
