package com.example.adminannouncementservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.adminannouncementservice.dto.AnnouncementRequest;
import com.example.adminannouncementservice.dto.AnnouncementResponse;
import com.example.adminannouncementservice.entity.Announcement;
import com.example.adminannouncementservice.exception.ResourceNotFoundException;
import com.example.adminannouncementservice.repository.AnnouncementRepository;
import com.example.adminannouncementservice.security.AuthenticatedUser;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AnnouncementServiceTest {

  @Mock private AnnouncementRepository announcementRepository;

  @InjectMocks private AnnouncementService announcementService;

  @Test
  void create_savesAnnouncementWithPosterInfo() {
    AnnouncementRequest req = new AnnouncementRequest();
    req.setText("Road closure this weekend.");
    AuthenticatedUser admin =
        new AuthenticatedUser(1L, "admin@cityfix.com", "Alice Admin", "ADMIN");

    when(announcementRepository.save(any(Announcement.class)))
        .thenAnswer(
            inv -> {
              Announcement a = inv.getArgument(0);
              a.setId(9L);
              return a;
            });

    AnnouncementResponse response = announcementService.create(req, admin);

    assertEquals(9L, response.getId());
    assertEquals("Road closure this weekend.", response.getText());
    assertEquals(1L, response.getPostedByUserId());
    assertEquals("Alice Admin", response.getPostedByName());
  }

  @Test
  void update_notFound_throwsResourceNotFound() {
    AnnouncementRequest req = new AnnouncementRequest();
    req.setText("update");
    when(announcementRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> announcementService.update(999L, req));
  }

  @Test
  void update_existing_changesText() {
    Announcement existing = new Announcement();
    existing.setId(1L);
    existing.setText("Old text");

    AnnouncementRequest req = new AnnouncementRequest();
    req.setText("New text");

    when(announcementRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(announcementRepository.save(any(Announcement.class)))
        .thenAnswer(inv -> inv.getArgument(0));

    AnnouncementResponse response = announcementService.update(1L, req);

    assertEquals("New text", response.getText());
  }

  @Test
  void delete_notFound_throwsResourceNotFound() {
    when(announcementRepository.findById(42L)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> announcementService.delete(42L));
  }

  @Test
  void delete_existing_removesIt() {
    Announcement existing = new Announcement();
    existing.setId(2L);
    when(announcementRepository.findById(2L)).thenReturn(Optional.of(existing));

    announcementService.delete(2L);

    verify(announcementRepository).delete(existing);
  }
}
