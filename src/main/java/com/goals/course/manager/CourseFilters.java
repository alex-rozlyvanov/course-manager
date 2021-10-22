package com.goals.course.manager;

import com.goals.course.manager.dao.enums.Roles;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

public enum CourseFilters {
    ALL(List.of(Roles.values())),
    STUDENT(List.of(Roles.STUDENT)),
    INSTRUCTOR(List.of(Roles.INSTRUCTOR));

    private final List<GrantedAuthority> applicableRoles;

    CourseFilters(final List<Roles> applicableRoles) {
        this.applicableRoles = applicableRoles
                .stream()
                .map(role -> (GrantedAuthority) new SimpleGrantedAuthority(role.name()))
                .toList();
    }

    public boolean isApplicable(final Authentication authentication) {
        return authentication.getAuthorities()
                .stream()
                .anyMatch(applicableRoles::contains);
    }
}
