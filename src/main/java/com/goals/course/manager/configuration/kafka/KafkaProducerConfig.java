package com.goals.course.manager.configuration.kafka;

import com.goals.course.avro.CourseAssignmentEventAvro;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {
    @Value(value = "${app.kafka.bootstrapAddress}")
    private String bootstrapAddress;

    @Value(value = "${app.kafka.schema-registry}")
    private String schemaRegistry;

    @Bean
    public ProducerFactory<String, CourseAssignmentEventAvro> producerFactoryWithJsonSchema() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(AbstractKafkaSchemaSerDeConfig.AUTO_REGISTER_SCHEMAS, true);
        configProps.put(AbstractKafkaSchemaSerDeConfig.KEY_SUBJECT_NAME_STRATEGY, io.confluent.kafka.serializers.subject.TopicRecordNameStrategy.class);
        configProps.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistry);

        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        configProps.put(ProducerConfig.RETRIES_CONFIG, "3");
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);

        return new DefaultKafkaProducerFactory<>(configProps);
    }


    @Bean
    public KafkaTemplate<String, CourseAssignmentEventAvro> kafkaTemplateWithJsonSchema() {
        return new KafkaTemplate<>(producerFactoryWithJsonSchema());
    }
}
