package com.meraki.website.security;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.meraki.website.entity.User;
import com.meraki.website.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring("Bearer ".length()).trim();
        if (!StringUtils.hasText(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            DecodedJWT jwt = jwtService.verify(token);
            String userId = jwt.getClaim("x-user").asString();
            String role = jwt.getClaim("x-role").asString();

            if (!StringUtils.hasText(userId) || !StringUtils.hasText(role)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            User user = userRepository.findById(userId).orElse(null);
            if (user == null || Boolean.FALSE.equals(user.getIsVerified())) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            request.setAttribute("x-user", userId);
            request.setAttribute("x-role", role);

            String path = request.getRequestURI();
            if (Boolean.TRUE.equals(user.getMustChangePassword())
                    && !path.startsWith("/auth/change-password")) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userId,
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_" + role))
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}

