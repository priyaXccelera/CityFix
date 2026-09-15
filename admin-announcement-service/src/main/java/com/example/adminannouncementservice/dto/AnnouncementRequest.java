package com.example.adminannouncementservice.dto;

import jakarta.validation.constraints.NotBlank;

public class AnnouncementRequest {

  @NotBlank(message = "text is required")
  private String text;

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }
}
