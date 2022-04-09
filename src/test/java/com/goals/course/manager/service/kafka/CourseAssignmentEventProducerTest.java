package com.goals.course.manager.service.kafka;

import com.goals.course.avro.CourseAssignmentEventAvro;
import com.goals.course.manager.dao.entity.*;
import com.goals.course.manager.mapper.kafka.CourseAssignmentEventMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
class CourseAssignmentEventProducerTest {

    @Mock
    private KafkaTemplate<String, CourseAssignmentEventAvro> mockKafkaTemplate;
    @Mock
    private CourseAssignmentEventMapper mockCourseAssignmentEventMapper;
    @InjectMocks
    private CourseAssignmentEventProducer service;

    @Test
    void generateStudentAssignmentEventAsync_callMapFromCourseStudent() {
        // GIVEN
        final var courseStudent = buildCourseStudent(
                "00000000-0000-0000-0000-000000000001",
                "00000000-0000-0000-0000-000000000002",
                "00000000-0000-0000-0000-000000000003"
        );

        final var mockFuture = (ListenableFuture<SendResult<String, CourseAssignmentEventAvro>>) mock(ListenableFuture.class);
        when(mockKafkaTemplate.send(any(), any())).thenReturn(mockFuture);

        // WHEN
        service.generateStudentAssignmentEventAsync(courseStudent);

        // THEN
        verify(mockCourseAssignmentEventMapper).mapFromCourseStudent(courseStudent);
    }

    private CourseStudent buildCourseStudent(final String id,
                                             final String studentId,
                                             final String courseId) {
        return new CourseStudent()
                .setId(UUID.fromString(id))
                .setStudent(new Student().setId(UUID.fromString(studentId)))
                .setCourse(new Course().setId(UUID.fromString(courseId)));
    }

    @Test
    void generateEventAsync_callSend() {
        // GIVEN
        ReflectionTestUtils.setField(service, "topicName", "test-topic-name");

        final var courseAssignmentEvent = buildCourseAssignmentEvent("00000000-0000-0000-0000-000000000001");
        when(mockCourseAssignmentEventMapper.mapFromCourseStudent(any())).thenReturn(courseAssignmentEvent);

        final var mockFuture = (ListenableFuture<SendResult<String, CourseAssignmentEventAvro>>) mock(ListenableFuture.class);
        when(mockKafkaTemplate.send(any(), any())).thenReturn(mockFuture);

        // WHEN
        service.generateStudentAssignmentEventAsync(null);

        // THEN
        verify(mockKafkaTemplate).send("test-topic-name", courseAssignmentEvent);
    }

    private CourseAssignmentEventAvro buildCourseAssignmentEvent(final String id) {
        return CourseAssignmentEventAvro.newBuilder().setId(id).build();
//        return CourseAssignmentEvent.builder()
//                .id(UUID.fromString(id))
//                .build();
    }

    @Test
    void generateInstructorAssignmentEventAsync_callMapFromCourseStudent() {
        // GIVEN
        final var courseInstructor = buildCourseInstructor(
                "00000000-0000-0000-0000-000000000001",
                "00000000-0000-0000-0000-000000000002",
                "00000000-0000-0000-0000-000000000003"
        );

        final var mockFuture = (ListenableFuture<SendResult<String, CourseAssignmentEventAvro>>) mock(ListenableFuture.class);
        when(mockKafkaTemplate.send(any(), any())).thenReturn(mockFuture);

        // WHEN
        service.generateInstructorAssignmentEventAsync(courseInstructor);

        // THEN
        verify(mockCourseAssignmentEventMapper).mapFromCourseInstructor(courseInstructor);
    }

    private CourseInstructor buildCourseInstructor(final String id,
                                                   final String instructorId,
                                                   final String courseId) {
        return new CourseInstructor()
                .setId(UUID.fromString(id))
                .setInstructor(new Instructor().setId(UUID.fromString(instructorId)))
                .setCourse(new Course().setId(UUID.fromString(courseId)));
    }

    @Test
    void generateInstructorAssignmentEventAsync_callSend() {
        // GIVEN
        ReflectionTestUtils.setField(service, "topicName", "test-topic-name");

        final var courseAssignmentEvent = buildCourseAssignmentEvent("00000000-0000-0000-0000-000000000002");
        when(mockCourseAssignmentEventMapper.mapFromCourseInstructor(any())).thenReturn(courseAssignmentEvent);

        final var mockFuture = (ListenableFuture<SendResult<String, CourseAssignmentEventAvro>>) mock(ListenableFuture.class);
        when(mockKafkaTemplate.send(any(), any())).thenReturn(mockFuture);

        // WHEN
        service.generateInstructorAssignmentEventAsync(null);

        // THEN
        verify(mockKafkaTemplate).send("test-topic-name", courseAssignmentEvent);
    }

}
