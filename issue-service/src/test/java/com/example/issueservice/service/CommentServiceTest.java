package com.example.issueservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.issueservice.dto.CommentRequest;
import com.example.issueservice.dto.CommentResponse;
import com.example.issueservice.entity.Comment;
import com.example.issueservice.entity.Issue;
import com.example.issueservice.exception.ResourceNotFoundException;
import com.example.issueservice.repository.CommentRepository;
import com.example.issueservice.repository.IssueRepository;
import com.example.issueservice.security.AuthenticatedUser;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

  @Mock private CommentRepository commentRepository;

  @Mock private IssueRepository issueRepository;

  @InjectMocks private CommentService commentService;

  @Test
  void add_withUnknownIssue_throwsResourceNotFound() {
    CommentRequest req = new CommentRequest();
    req.setText("hello");
    AuthenticatedUser user = new AuthenticatedUser(1L, "admin@cityfix.com", "Alice Admin", "ADMIN");

    when(issueRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> commentService.add(999L, req, user));
  }

  @Test
  void add_withValidIssue_savesCommentWithPosterRole() {
    CommentRequest req = new CommentRequest();
    req.setText("We are looking into this.");
    AuthenticatedUser user = new AuthenticatedUser(1L, "admin@cityfix.com", "Alice Admin", "ADMIN");

    Issue issue = new Issue();
    issue.setId(5L);

    when(issueRepository.findById(5L)).thenReturn(Optional.of(issue));
    when(commentRepository.save(any(Comment.class)))
        .thenAnswer(
            inv -> {
              Comment c = inv.getArgument(0);
              c.setId(1L);
              return c;
            });

    CommentResponse response = commentService.add(5L, req, user);

    assertEquals("We are looking into this.", response.getText());
    assertEquals("ADMIN", response.getPostedByRole());
    assertEquals(1L, response.getPostedByUserId());
  }

  @Test
  void list_withUnknownIssue_throwsResourceNotFound() {
    when(issueRepository.existsById(42L)).thenReturn(false);
    assertThrows(
        ResourceNotFoundException.class,
        () -> commentService.list(42L, org.springframework.data.domain.PageRequest.of(0, 20)));
  }
}
