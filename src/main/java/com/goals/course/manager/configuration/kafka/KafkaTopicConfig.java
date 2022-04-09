package com.goals.course.manager.configuration.kafka;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {

    @Value(value = "${app.kafka.bootstrapAddress}")
    private String bootstrapAddress;
    @Value(value = "${app.kafka.topic.course-assignment.name}")
    private String courseAssignmentTopicName;
    @Value(value = "${app.kafka.topic.course-assignment.partitions}")
    private int courseAssignmentTopicPartitions;
    @Value(value = "${app.kafka.topic.course-assignment.replicas}")
    private int courseAssignmentTopicReplicas;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        final Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic studentAssignmentTopic() {
        return TopicBuilder.name(courseAssignmentTopicName)
                .partitions(courseAssignmentTopicPartitions)
                .replicas(courseAssignmentTopicReplicas)
                .build();
    }
}
