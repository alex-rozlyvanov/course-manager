package com.goals.course.manager.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goals.course.manager.dto.RoleDTO;
import com.goals.course.manager.dto.UserDTO;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenService {

    private final ObjectMapper objectMapper = new ObjectMapper().configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
    @Value("${app.jwt.access.secret}")
    private String accessTokenSecret;

    public boolean validate(final String token) {
        try {
            Jwts.parser().setSigningKey(accessTokenSecret).parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token - {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token - {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token - {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty - {}", ex.getMessage());
        }
        return false;
    }

    public UserDTO getUserDTO(final String token) {
        final var body = Jwts.parser()
                .setSigningKey(accessTokenSecret)
                .parseClaimsJws(token)
                .getBody();
        final var userId = UUID.fromString(body.get("userId", String.class));
        final var roles = getRoles(body);

        return UserDTO.builder()
                .id(userId)
                .roles(roles)
                .build();
    }

    @SuppressWarnings("unchecked")
    private List<RoleDTO> getRoles(final Claims claims) {
        final List<Map<String, Object>> rolesRawList = claims.get("roles", List.class);

        return rolesRawList.stream()
                .map(r -> objectMapper.convertValue(r, RoleDTO.class))
                .toList();

    }

}
