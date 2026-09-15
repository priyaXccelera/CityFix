package com.example.adminannouncementservice.service;

import com.example.adminannouncementservice.dto.AnnouncementRequest;
import com.example.adminannouncementservice.dto.AnnouncementResponse;
import com.example.adminannouncementservice.entity.Announcement;
import com.example.adminannouncementservice.exception.ResourceNotFoundException;
import com.example.adminannouncementservice.repository.AnnouncementRepository;
import com.example.adminannouncementservice.security.AuthenticatedUser;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AnnouncementService {

  private final AnnouncementRepository announcementRepository;

  public AnnouncementService(AnnouncementRepository announcementRepository) {
    this.announcementRepository = announcementRepository;
  }

  public AnnouncementResponse create(AnnouncementRequest request, AuthenticatedUser user) {
    Announcement a = new Announcement();
    a.setText(request.getText());
    a.setPostedByUserId(user.getUserId());
    a.setPostedByName(user.getName());
    return AnnouncementResponse.from(announcementRepository.save(a));
  }

  public AnnouncementResponse update(Long id, AnnouncementRequest request) {
    Announcement a =
        announcementRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Announcement not found: " + id));
    a.setText(request.getText());
    a.setUpdatedAt(LocalDateTime.now());
    return AnnouncementResponse.from(announcementRepository.save(a));
  }

  public void delete(Long id) {
    Announcement a =
        announcementRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Announcement not found: " + id));
    announcementRepository.delete(a);
  }

  public AnnouncementResponse get(Long id) {
    return AnnouncementResponse.from(
        announcementRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Announcement not found: " + id)));
  }

  public Page<AnnouncementResponse> list(Pageable pageable) {
    return announcementRepository.findAll(pageable).map(AnnouncementResponse::from);
  }
}
