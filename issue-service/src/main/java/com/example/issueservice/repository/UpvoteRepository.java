package com.example.issueservice.repository;

import com.example.issueservice.entity.Upvote;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UpvoteRepository extends JpaRepository<Upvote, Long> {

  long countByIssueId(Long issueId);

  Optional<Upvote> findByIssueIdAndUserId(Long issueId, Long userId);

  boolean existsByIssueIdAndUserId(Long issueId, Long userId);

  @Query(
      "select u.issue.id from Upvote u where u.issue.status not in ('RESOLVED','REJECTED') "
          + "group by u.issue.id order by count(u) desc")
  List<Long> topUpvotedUnresolvedIssueIds(Pageable pageable);
}
