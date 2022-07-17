package com.goals.course.manager.service.kafka;

import com.goals.course.avro.CourseAssignmentEventAvro;
import com.goals.course.manager.dao.entity.CourseInstructor;
import com.goals.course.manager.dao.entity.CourseStudent;
import com.goals.course.manager.mapper.kafka.CourseAssignmentEventMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseAssignmentEventProducer {

    private final KafkaTemplate<String, CourseAssignmentEventAvro> kafkaTemplate;
    private final CourseAssignmentEventMapper courseAssignmentEventMapper;
    @Value("${app.kafka.topic.course-assignment.name}")
    private String topicName;

    public void generateStudentAssignmentEventAsync(final CourseStudent courseStudent) {
        final var courseAssignmentEvent = courseAssignmentEventMapper.mapFromCourseStudent(courseStudent);
        sendMessageAsync(courseAssignmentEvent);
    }

    public void generateInstructorAssignmentEventAsync(final CourseInstructor courseInstructor) {
        final var courseAssignmentEvent = courseAssignmentEventMapper.mapFromCourseInstructor(courseInstructor);
        sendMessageAsync(courseAssignmentEvent);
    }

    private void sendMessageAsync(final CourseAssignmentEventAvro message) {
        final var future = kafkaTemplate.send(topicName, message);

        future.addCallback(new ListenableFutureCallback<>() {

            public void onSuccess(final SendResult<String, CourseAssignmentEventAvro> result) {
                log.info("Sent message=[%s] with offset=[%d]".formatted(message, result.getRecordMetadata().offset()));
            }


            public void onFailure(final Throwable ex) {
                log.info("Unable to send message=[%s] due to : %s".formatted(message, ex.getMessage()));
            }
        });
    }
}
