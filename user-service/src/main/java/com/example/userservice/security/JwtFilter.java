package com.example.userservice.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;
  private final UserDetailsServiceImpl userDetailsService;

  public JwtFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService) {
    this.jwtUtil = jwtUtil;
    this.userDetailsService = userDetailsService;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String header = request.getHeader("Authorization");

    if (header != null && header.startsWith("Bearer ")) {
      String token = header.substring(7);
      try {
        if (jwtUtil.isValid(token)) {
          Claims claims = jwtUtil.parseClaims(token);
          String email = claims.getSubject();

          if (SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
              UserDetails userDetails = userDetailsService.loadUserByUsername(email);
              UsernamePasswordAuthenticationToken authToken =
                  new UsernamePasswordAuthenticationToken(
                      userDetails, null, userDetails.getAuthorities());
              authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
              SecurityContextHolder.getContext().setAuthentication(authToken);
            } catch (UsernameNotFoundException ignored) {
              // deactivated or missing user -> leave unauthenticated, SecurityConfig returns
              // 401/403
            }
          }
        }
      } catch (Exception ignored) {
        // invalid/expired token -> leave unauthenticated
      }
    }

    filterChain.doFilter(request, response);
  }
}
