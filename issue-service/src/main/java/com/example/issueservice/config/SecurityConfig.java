package com.example.issueservice.config;

import com.example.issueservice.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final JwtFilter jwtFilter;

  public SecurityConfig(JwtFilter jwtFilter) {
    this.jwtFilter = jwtFilter;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
        .cors(Customizer.withDefaults())
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(
                        "/actuator/health",
                        "/docs/**",
                        "/api-docs/**",
                        "/swagger-ui/**",
                        "/v3/api-docs/**")
                    .permitAll()
                    .requestMatchers(
                        org.springframework.http.HttpMethod.POST, "/api/v1/departments")
                    .hasAnyRole("ADMIN", "SUPER_ADMIN")
                    .requestMatchers(
                        org.springframework.http.HttpMethod.PUT, "/api/v1/departments/**")
                    .hasAnyRole("ADMIN", "SUPER_ADMIN")
                    .requestMatchers(
                        org.springframework.http.HttpMethod.DELETE, "/api/v1/departments/**")
                    .hasAnyRole("ADMIN", "SUPER_ADMIN")
                    .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/v1/categories")
                    .hasAnyRole("ADMIN", "SUPER_ADMIN")
                    .requestMatchers(
                        org.springframework.http.HttpMethod.PUT, "/api/v1/categories/**")
                    .hasAnyRole("ADMIN", "SUPER_ADMIN")
                    .requestMatchers(
                        org.springframework.http.HttpMethod.DELETE, "/api/v1/categories/**")
                    .hasAnyRole("ADMIN", "SUPER_ADMIN")
                    .requestMatchers("/api/v1/issues/analytics/**")
                    .hasAnyRole("ADMIN", "SUPER_ADMIN")
                    .requestMatchers(
                        org.springframework.http.HttpMethod.PUT, "/api/v1/issues/*/assign")
                    .hasAnyRole("ADMIN", "SUPER_ADMIN")
                    .requestMatchers(
                        org.springframework.http.HttpMethod.PUT, "/api/v1/issues/*/status")
                    .hasAnyRole("ADMIN", "SUPER_ADMIN")
                    .requestMatchers(
                        org.springframework.http.HttpMethod.PUT, "/api/v1/issues/*/priority")
                    .hasAnyRole("ADMIN", "SUPER_ADMIN")
                    .anyRequest()
                    .authenticated())
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }
}
