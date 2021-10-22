package com.goals.course.manager.service.implementation;

import com.goals.course.manager.configuration.JournalURL;
import com.goals.course.manager.dto.GradeDTO;
import com.goals.course.manager.service.GradeProvider;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;

import static java.util.Optional.ofNullable;

@Slf4j
@Service
@AllArgsConstructor
public class GradeProviderImpl implements GradeProvider {
    private final RestTemplate restTemplate;
    private final JournalURL journalURL;

    @Override
    public Optional<GradeDTO> findGradeByLessonIdAndStudentId(final UUID lessonId, final UUID studentId) {
        log.info("Getting grade by lesson '{}' and student '{}'", lessonId, studentId);
        final var url = journalURL.gradeByLessonIdAndStudentId(lessonId, studentId);
        return getForObject(url);
    }

    private Optional<GradeDTO> getForObject(final String url) {
        try {
            return ofNullable(restTemplate.getForObject(url, GradeDTO.class));
        } catch (HttpClientErrorException.NotFound ex) {
            return Optional.empty();
        }
    }
}
