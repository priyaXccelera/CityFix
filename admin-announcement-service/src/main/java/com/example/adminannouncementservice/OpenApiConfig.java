package com.example.adminannouncementservice;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI adminAnnouncementServiceOpenAPI() {
    return new OpenAPI().addServersItem(new Server().url("/admin-announcement-service"));
  }
}
