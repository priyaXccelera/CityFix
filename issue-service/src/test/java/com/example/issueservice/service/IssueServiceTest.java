package com.example.issueservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.issueservice.dto.IssueRequest;
import com.example.issueservice.dto.IssueResponse;
import com.example.issueservice.entity.*;
import com.example.issueservice.exception.ResourceNotFoundException;
import com.example.issueservice.repository.*;
import com.example.issueservice.security.AuthenticatedUser;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IssueServiceTest {

  @Mock private IssueRepository issueRepository;
  @Mock private IssueCategoryRepository categoryRepository;
  @Mock private DepartmentRepository departmentRepository;
  @Mock private UpvoteRepository upvoteRepository;
  @Mock private CommentRepository commentRepository;

  @InjectMocks private IssueService issueService;

  @Test
  void report_withUnknownCategory_throwsResourceNotFound() {
    IssueRequest req = new IssueRequest();
    req.setCategoryId(99L);
    req.setTitle("Pothole");
    req.setDescription("desc");
    req.setArea("Downtown");

    AuthenticatedUser user = new AuthenticatedUser(2L, "bob@cityfix.com", "Bob", "USER");

    when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> issueService.report(req, user));
  }

  @Test
  void report_withValidCategory_defaultsPriorityFromCategoryAndSaves() {
    IssueRequest req = new IssueRequest();
    req.setCategoryId(1L);
    req.setTitle("Pothole on 5th");
    req.setDescription("Deep pothole");
    req.setArea("Downtown");

    Department dept = new Department();
    dept.setId(1L);
    IssueCategory category = new IssueCategory();
    category.setId(1L);
    category.setName("Pothole");
    category.setDepartment(dept);
    category.setDefaultPriority(Priority.HIGH);

    AuthenticatedUser user = new AuthenticatedUser(2L, "bob@cityfix.com", "Bob Reporter", "USER");

    when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
    when(issueRepository.save(any(Issue.class)))
        .thenAnswer(
            inv -> {
              Issue i = inv.getArgument(0);
              i.setId(50L);
              return i;
            });
    when(upvoteRepository.countByIssueId(50L)).thenReturn(0L);
    when(commentRepository.countByIssueId(50L)).thenReturn(0L);

    IssueResponse response = issueService.report(req, user);

    assertEquals(50L, response.getId());
    assertEquals(Priority.HIGH, response.getPriority());
    assertEquals(IssueStatus.REPORTED, response.getStatus());
    assertEquals(2L, response.getReportedByUserId());
  }

  @Test
  void assignDepartment_movesReportedIssueToAssignedStatus() {
    Issue issue = new Issue();
    issue.setId(3L);
    issue.setStatus(IssueStatus.REPORTED);
    IssueCategory category = new IssueCategory();
    category.setId(1L);
    issue.setCategory(category);

    Department dept = new Department();
    dept.setId(2L);
    dept.setName("Sanitation");

    when(issueRepository.findById(3L)).thenReturn(Optional.of(issue));
    when(departmentRepository.findById(2L)).thenReturn(Optional.of(dept));
    when(issueRepository.save(any(Issue.class))).thenAnswer(inv -> inv.getArgument(0));
    when(upvoteRepository.countByIssueId(3L)).thenReturn(0L);
    when(commentRepository.countByIssueId(3L)).thenReturn(0L);

    IssueResponse response = issueService.assignDepartment(3L, 2L);

    assertEquals(IssueStatus.ASSIGNED, response.getStatus());
    assertEquals("Sanitation", response.getAssignedDepartmentName());
  }

  @Test
  void updateStatus_toResolved_setsResolvedAtTimestamp() {
    Issue issue = new Issue();
    issue.setId(4L);
    issue.setStatus(IssueStatus.IN_PROGRESS);
    IssueCategory category = new IssueCategory();
    category.setId(1L);
    issue.setCategory(category);

    when(issueRepository.findById(4L)).thenReturn(Optional.of(issue));
    when(issueRepository.save(any(Issue.class))).thenAnswer(inv -> inv.getArgument(0));
    when(upvoteRepository.countByIssueId(4L)).thenReturn(0L);
    when(commentRepository.countByIssueId(4L)).thenReturn(0L);

    IssueResponse response = issueService.updateStatus(4L, IssueStatus.RESOLVED);

    assertEquals(IssueStatus.RESOLVED, response.getStatus());
    assertNotNull(response.getResolvedAt());
  }
}
