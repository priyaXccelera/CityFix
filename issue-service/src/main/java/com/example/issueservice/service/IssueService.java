package com.example.issueservice.service;

import com.example.issueservice.dto.*;
import com.example.issueservice.entity.*;
import com.example.issueservice.exception.ResourceNotFoundException;
import com.example.issueservice.repository.*;
import com.example.issueservice.security.AuthenticatedUser;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class IssueService {

  private final IssueRepository issueRepository;
  private final IssueCategoryRepository categoryRepository;
  private final DepartmentRepository departmentRepository;
  private final UpvoteRepository upvoteRepository;
  private final CommentRepository commentRepository;

  public IssueService(
      IssueRepository issueRepository,
      IssueCategoryRepository categoryRepository,
      DepartmentRepository departmentRepository,
      UpvoteRepository upvoteRepository,
      CommentRepository commentRepository) {
    this.issueRepository = issueRepository;
    this.categoryRepository = categoryRepository;
    this.departmentRepository = departmentRepository;
    this.upvoteRepository = upvoteRepository;
    this.commentRepository = commentRepository;
  }

  public IssueResponse report(IssueRequest request, AuthenticatedUser user) {
    IssueCategory category =
        categoryRepository
            .findById(request.getCategoryId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Category not found: " + request.getCategoryId()));

    Issue issue = new Issue();
    issue.setTitle(request.getTitle());
    issue.setDescription(request.getDescription());
    issue.setCategory(category);
    issue.setArea(request.getArea());
    issue.setLatitude(request.getLatitude());
    issue.setLongitude(request.getLongitude());
    issue.setPhotoReference(request.getPhotoReference());
    issue.setStatus(IssueStatus.REPORTED);
    issue.setPriority(category.getDefaultPriority());
    issue.setReportedByUserId(user.getUserId());
    issue.setReportedByName(user.getName());

    Issue saved = issueRepository.save(issue);
    return toResponse(saved);
  }

  public IssueResponse get(Long id) {
    Issue issue =
        issueRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Issue not found: " + id));
    return toResponse(issue);
  }

  public Page<IssueResponse> browse(
      Long departmentId,
      Long categoryId,
      IssueStatus status,
      Priority priority,
      String area,
      Pageable pageable) {
    Specification<Issue> spec = buildSpec(departmentId, categoryId, status, priority, area);
    return issueRepository.findAll(spec, pageable).map(this::toResponse);
  }

  public Page<IssueResponse> myIssues(Long userId, Pageable pageable) {
    return issueRepository.findByReportedByUserId(userId, pageable).map(this::toResponse);
  }

  public IssueResponse assignDepartment(Long id, Long departmentId) {
    Issue issue =
        issueRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Issue not found: " + id));
    Department department =
        departmentRepository
            .findById(departmentId)
            .orElseThrow(
                () -> new ResourceNotFoundException("Department not found: " + departmentId));
    issue.setAssignedDepartment(department);
    if (issue.getStatus() == IssueStatus.REPORTED || issue.getStatus() == IssueStatus.IN_REVIEW) {
      issue.setStatus(IssueStatus.ASSIGNED);
    }
    issue.setUpdatedAt(LocalDateTime.now());
    return toResponse(issueRepository.save(issue));
  }

  public IssueResponse updateStatus(Long id, IssueStatus status) {
    Issue issue =
        issueRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Issue not found: " + id));
    issue.setStatus(status);
    issue.setUpdatedAt(LocalDateTime.now());
    if (status == IssueStatus.RESOLVED) {
      issue.setResolvedAt(LocalDateTime.now());
    } else {
      issue.setResolvedAt(null);
    }
    return toResponse(issueRepository.save(issue));
  }

  public IssueResponse updatePriority(Long id, Priority priority) {
    Issue issue =
        issueRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Issue not found: " + id));
    issue.setPriority(priority);
    issue.setUpdatedAt(LocalDateTime.now());
    return toResponse(issueRepository.save(issue));
  }

  public IssueResponse toResponse(Issue issue) {
    long upvotes = upvoteRepository.countByIssueId(issue.getId());
    long comments = commentRepository.countByIssueId(issue.getId());
    return IssueResponse.from(issue, upvotes, comments);
  }

  public List<Issue> findAllByIds(List<Long> ids) {
    List<Issue> result = new ArrayList<>();
    for (Long id : ids) {
      issueRepository.findById(id).ifPresent(result::add);
    }
    return result;
  }

  private Specification<Issue> buildSpec(
      Long departmentId, Long categoryId, IssueStatus status, Priority priority, String area) {
    return (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();
      if (departmentId != null) {
        predicates.add(cb.equal(root.get("assignedDepartment").get("id"), departmentId));
      }
      if (categoryId != null) {
        predicates.add(cb.equal(root.get("category").get("id"), categoryId));
      }
      if (status != null) {
        predicates.add(cb.equal(root.get("status"), status));
      }
      if (priority != null) {
        predicates.add(cb.equal(root.get("priority"), priority));
      }
      if (area != null && !area.isBlank()) {
        predicates.add(cb.equal(cb.lower(root.get("area")), area.toLowerCase()));
      }
      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }
}
