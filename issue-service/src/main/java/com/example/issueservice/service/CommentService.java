package com.example.issueservice.service;

import com.example.issueservice.dto.CommentRequest;
import com.example.issueservice.dto.CommentResponse;
import com.example.issueservice.entity.Comment;
import com.example.issueservice.entity.Issue;
import com.example.issueservice.exception.ResourceNotFoundException;
import com.example.issueservice.repository.CommentRepository;
import com.example.issueservice.repository.IssueRepository;
import com.example.issueservice.security.AuthenticatedUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

  private final CommentRepository commentRepository;
  private final IssueRepository issueRepository;

  public CommentService(CommentRepository commentRepository, IssueRepository issueRepository) {
    this.commentRepository = commentRepository;
    this.issueRepository = issueRepository;
  }

  public CommentResponse add(Long issueId, CommentRequest request, AuthenticatedUser user) {
    Issue issue =
        issueRepository
            .findById(issueId)
            .orElseThrow(() -> new ResourceNotFoundException("Issue not found: " + issueId));

    Comment comment = new Comment();
    comment.setIssue(issue);
    comment.setText(request.getText());
    comment.setPostedByUserId(user.getUserId());
    comment.setPostedByName(user.getName());
    comment.setPostedByRole(user.getRole());

    return CommentResponse.from(commentRepository.save(comment));
  }

  public Page<CommentResponse> list(Long issueId, Pageable pageable) {
    if (!issueRepository.existsById(issueId)) {
      throw new ResourceNotFoundException("Issue not found: " + issueId);
    }
    return commentRepository.findByIssueId(issueId, pageable).map(CommentResponse::from);
  }
}
