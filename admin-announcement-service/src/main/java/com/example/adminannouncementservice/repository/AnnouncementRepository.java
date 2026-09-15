package com.example.adminannouncementservice.repository;

import com.example.adminannouncementservice.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {}
