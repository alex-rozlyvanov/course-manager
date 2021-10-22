package com.goals.course.manager.configuration;

import com.goals.course.manager.dto.UserDTO;
import com.goals.course.manager.service.JwtTokenService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@AllArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;

    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    final FilterChain chain) throws ServletException, IOException {
        // Get authorization header and validate
        final var header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(header) || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        // Get jwt token and validate
        final var token = header.split(" ")[1].trim();
        if (!jwtTokenService.validate(token)) {
            chain.doFilter(request, response);
            return;
        }

        setUserIdentityOnSecurityContext(request, token);

        chain.doFilter(request, response);
    }

    // Get user identity and set it on the spring security context
    private void setUserIdentityOnSecurityContext(HttpServletRequest request, String token) {
        final var userDetails = getUserDetails(token);
        final var authentication = getAuthentication(request, userDetails);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private UserDTO getUserDetails(final String token) {
        return jwtTokenService.getUserDTO(token);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(final HttpServletRequest request,
                                                                  final UserDTO userDetails) {
        final var authorities = userDetails.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getTitle()))
                .toList();
        final var authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null,
                authorities
        );

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        return authentication;
    }
}
