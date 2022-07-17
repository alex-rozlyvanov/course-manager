package com.goals.course.manager.service;

import com.goals.course.manager.configuration.JournalURL;
import com.goals.course.manager.dto.GradeDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class GradeProvider {
    private final WebClient webClient;
    private final JournalURL journalURL;

    public Mono<GradeDTO> findGradeByLessonIdAndStudentId(final UUID lessonId, final UUID studentId) {
        log.info("Getting grade by lesson '{}' and student '{}'", lessonId, studentId);
        final var url = journalURL.gradeByLessonIdAndStudentId(lessonId, studentId);
        return getForObject(url);
    }

    private Mono<GradeDTO> getForObject(final String url) {
        try {
            return webClient.get()
                    .uri(url)
                    .retrieve()
                    .onStatus(HttpStatus::isError, r -> Mono.empty())
                    .bodyToMono(GradeDTO.class)
                    .onErrorResume(throwable -> Mono.empty());
        } catch (HttpClientErrorException | WebClientResponseException ex) {
            log.error("Exception during Grades fetch", ex);
            return Mono.empty();
        }
    }
}
