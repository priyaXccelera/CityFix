package com.example.issueservice.service;

import com.example.issueservice.dto.UpvoteResponse;
import com.example.issueservice.entity.Issue;
import com.example.issueservice.entity.Upvote;
import com.example.issueservice.exception.BadRequestException;
import com.example.issueservice.exception.ResourceNotFoundException;
import com.example.issueservice.repository.IssueRepository;
import com.example.issueservice.repository.UpvoteRepository;
import org.springframework.stereotype.Service;

@Service
public class UpvoteService {

  private final UpvoteRepository upvoteRepository;
  private final IssueRepository issueRepository;

  public UpvoteService(UpvoteRepository upvoteRepository, IssueRepository issueRepository) {
    this.upvoteRepository = upvoteRepository;
    this.issueRepository = issueRepository;
  }

  public UpvoteResponse upvote(Long issueId, Long userId) {
    Issue issue =
        issueRepository
            .findById(issueId)
            .orElseThrow(() -> new ResourceNotFoundException("Issue not found: " + issueId));

    if (upvoteRepository.existsByIssueIdAndUserId(issueId, userId)) {
      throw new BadRequestException("User " + userId + " already upvoted issue " + issueId);
    }

    Upvote upvote = new Upvote();
    upvote.setIssue(issue);
    upvote.setUserId(userId);
    upvoteRepository.save(upvote);

    long count = upvoteRepository.countByIssueId(issueId);
    return new UpvoteResponse(issueId, count, true);
  }
}
