package com.goals.course.manager.dto;

import com.goals.course.manager.dao.enums.Roles;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@Jacksonized
public class UserDTO {
    private final UUID id;
    private final String username;
    private final String firstName;
    private final String lastName;
    private final boolean enabled;
    private final List<RoleDTO> roles;

    public boolean isStudent() {
        return hasRole(Roles.STUDENT);
    }

    public boolean isNotStudent() {
        return hasNoRole(Roles.STUDENT);
    }

    public boolean isInstructor() {
        return hasRole(Roles.INSTRUCTOR);
    }

    public boolean isAdmin() {
        return hasRole(Roles.ADMIN);
    }

    private boolean hasRole(final Roles role) {
        return getRoles()
                .stream()
                .anyMatch(r -> role.name().equals(r.getTitle()));
    }

    private boolean hasNoRole(final Roles role) {
        return getRoles()
                .stream()
                .noneMatch(r -> role.name().equals(r.getTitle()));
    }

    public boolean isNotAdmin() {
        return hasNoRole(Roles.ADMIN);
    }
}
