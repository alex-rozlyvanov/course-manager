package com.goals.course.manager.configuration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
@Service
public class JournalURL {

    @Value("${app.microservice.journal.url}")
    private String baseUrl;

    public String gradeByLessonIdAndStudentId(final UUID lessonId, final UUID studentId) {
        return baseUrl + "/api/journal/grades?lessonId=%s&studentId=%s".formatted(lessonId, studentId);
    }

}
