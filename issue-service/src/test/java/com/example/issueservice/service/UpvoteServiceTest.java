package com.example.issueservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.issueservice.dto.UpvoteResponse;
import com.example.issueservice.entity.Issue;
import com.example.issueservice.exception.BadRequestException;
import com.example.issueservice.exception.ResourceNotFoundException;
import com.example.issueservice.repository.IssueRepository;
import com.example.issueservice.repository.UpvoteRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpvoteServiceTest {

  @Mock private UpvoteRepository upvoteRepository;

  @Mock private IssueRepository issueRepository;

  @InjectMocks private UpvoteService upvoteService;

  @Test
  void upvote_withUnknownIssue_throwsResourceNotFound() {
    when(issueRepository.findById(999L)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> upvoteService.upvote(999L, 2L));
  }

  @Test
  void upvote_whenAlreadyUpvotedByUser_throwsBadRequest() {
    Issue issue = new Issue();
    issue.setId(1L);

    when(issueRepository.findById(1L)).thenReturn(Optional.of(issue));
    when(upvoteRepository.existsByIssueIdAndUserId(1L, 2L)).thenReturn(true);

    assertThrows(BadRequestException.class, () -> upvoteService.upvote(1L, 2L));
    verify(upvoteRepository, never()).save(any());
  }

  @Test
  void upvote_firstTime_savesAndReturnsIncrementedCount() {
    Issue issue = new Issue();
    issue.setId(1L);

    when(issueRepository.findById(1L)).thenReturn(Optional.of(issue));
    when(upvoteRepository.existsByIssueIdAndUserId(1L, 2L)).thenReturn(false);
    when(upvoteRepository.countByIssueId(1L)).thenReturn(3L);

    UpvoteResponse response = upvoteService.upvote(1L, 2L);

    assertEquals(1L, response.getIssueId());
    assertEquals(3L, response.getUpvoteCount());
    assertTrue(response.isUpvotedByCurrentUser());
    verify(upvoteRepository).save(any());
  }
}
