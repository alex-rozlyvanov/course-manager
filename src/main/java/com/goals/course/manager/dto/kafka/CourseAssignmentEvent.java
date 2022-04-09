package com.goals.course.manager.dto.kafka;

import com.goals.course.manager.dao.enums.Roles;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CourseAssignmentEvent {
    private UUID id;
    private UUID userId;
    private Roles role;
    private UUID courseId;
}
