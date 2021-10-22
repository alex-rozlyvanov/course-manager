package com.goals.course.manager.service.filtering.course.implementation;

import com.goals.course.manager.CourseFilters;
import com.goals.course.manager.dto.UserDTO;
import com.goals.course.manager.exception.FilteringIsNotAllowed;
import com.goals.course.manager.service.filtering.course.CourseFilteringService;
import com.goals.course.manager.service.filtering.course.model.CourseFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import static com.goals.course.manager.dao.enums.Roles.ADMIN;
import static java.util.Objects.isNull;

@Slf4j
@Service
public class CourseFilteringServiceImpl implements CourseFilteringService {

    @Override
    public CourseFilter getCourseFilter(final CourseFilters filter, final Authentication authentication) {
        if (isNull(filter)) {
            return CourseFilter.builder()
                    .filter(CourseFilters.ALL)
                    .build();
        }

        if (filter.isApplicable(authentication) || isAdmin(authentication)) {
            final var principal = (UserDTO) authentication.getPrincipal();

            return CourseFilter.builder()
                    .filter(filter)
                    .userId(principal.getId())
                    .build();
        }
        throw new FilteringIsNotAllowed("Filtering by '%s' is not allowed".formatted(filter));
    }

    private boolean isAdmin(final Authentication authentication) {
        return authentication.getAuthorities()
                .stream()
                .anyMatch(authority -> ADMIN.name().equals(authority.getAuthority()));
    }
}
