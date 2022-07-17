package com.goals.course.manager;

import com.goals.course.avro.CourseAssignmentEventAvro;
import com.goals.course.manager.dao.entity.Course;
import com.goals.course.manager.dao.entity.CourseInstructor;
import com.goals.course.manager.dao.entity.Instructor;
import com.goals.course.manager.dao.enums.Roles;
import com.goals.course.manager.dao.repository.CourseInstructorRepository;
import com.goals.course.manager.dao.repository.CourseRepository;
import com.goals.course.manager.dto.RoleDTO;
import com.goals.course.manager.dto.UserDTO;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.restassured.module.webtestclient.RestAssuredWebTestClient;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.*;
import java.util.stream.Stream;

@ActiveProfiles("contract-test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ContextConfiguration(initializers = {
        ContractVerifierBaseTestClass.TestContainersInitializer.class
})
@DirtiesContext
@AutoConfigureMessageVerifier
@AutoConfigureWebTestClient
public class ContractVerifierBaseTestClass {

    private static final PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer<>("postgres:14.0")
            .withDatabaseName("course_manager")
            .withUsername("sa")
            .withPassword("sa");
    private static final DockerImageName KAFKA_IMAGE_NAME = DockerImageName.parse("confluentinc/cp-kafka:7.0.1")
            .asCompatibleSubstituteFor("confluentinc/cp-kafka");
    private static final KafkaContainer kafka = new KafkaContainer(KAFKA_IMAGE_NAME);

    @Autowired
    private ApplicationContext context;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private CourseInstructorRepository courseInstructorRepository;
    @Value("${app.jwt.access.secret}")
    private String accessTokenSecret;
    @Value("${app.jwt.access.expiration}")
    private Duration accessTokenExpiration;
    @Value("${app.jwt.issuer}")
    private String jwtIssuer;
    @Value("${app.admin.username}")
    private String adminUsername;
    @Value("${app.admin.firstname}")
    private String adminFirstname;
    @Value("${app.admin.lastname}")
    private String adminLastname;

    @BeforeEach
    public void setup() {
        setupTestData();
        RestAssuredWebTestClient.applicationContextSetup(context);
    }

    private void setupTestData() {
        setupCourses();
    }

    private void setupCourses() {
        final var instructorId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        final var instructor = new Instructor().setId(instructorId);

        final var course = new Course()
                .setTitle("test title");
        courseRepository.save(course);

        final var courseInstructor = new CourseInstructor()
                .setInstructor(instructor)
                .setCourse(course);

        courseInstructorRepository.save(courseInstructor);
    }

    public String authToken() {
        final var admin = UserDTO.builder()
                .id(UUID.fromString("00000000-0000-0000-0000-000000000001"))
                .username(adminUsername)
                .firstName(adminFirstname)
                .lastName(adminLastname)
                .roles(buildRoles())
                .build();
        return "Bearer %s".formatted(generateAccessToken(admin));
    }

    private List<RoleDTO> buildRoles() {
        return Stream.of(Roles.values())
                .map(r -> RoleDTO.builder().title(r.name()).build())
                .toList();
    }

    public String generateAccessToken(final UserDTO userDTO) {
        final var claims = buildClaims(userDTO);

        return Jwts.builder()
                .setSubject(userDTO.getUsername())
                .setIssuer(jwtIssuer)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration.toMillis()))
                .signWith(SignatureAlgorithm.HS256, accessTokenSecret)
                .addClaims(claims)
                .compact();
    }

    private Map<String, Object> buildClaims(final UserDTO userDTO) {
        final var roles = userDTO.getRoles();
        return Map.of("userId", userDTO.getId().toString(), "roles", roles);
    }

    static class TestContainersInitializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            postgreSQLContainer.start();
            kafka.start();
            TestPropertyValues.of(
                    "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                    "spring.datasource.password=" + postgreSQLContainer.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    @TestConfiguration
    static class KafkaTestContainersConfiguration {

        @Bean
        public Map<String, Object> consumerConfigs() {
            Map<String, Object> props = new HashMap<>();
            props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
            props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            props.put(AbstractKafkaSchemaSerDeConfig.AUTO_REGISTER_SCHEMAS, true);
            props.put(AbstractKafkaSchemaSerDeConfig.KEY_SUBJECT_NAME_STRATEGY, io.confluent.kafka.serializers.subject.TopicRecordNameStrategy.class);
            props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);

            return props;
        }

        @Bean
        public ProducerFactory<String, CourseAssignmentEventAvro> producerFactoryWithJsonSchema() {
            Map<String, Object> configProps = new HashMap<>();
            configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
            configProps.put(AbstractKafkaSchemaSerDeConfig.AUTO_REGISTER_SCHEMAS, true);
            configProps.put(AbstractKafkaSchemaSerDeConfig.KEY_SUBJECT_NAME_STRATEGY, io.confluent.kafka.serializers.subject.TopicRecordNameStrategy.class);
            configProps.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "mock://testUrl");

            configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            configProps.put(ProducerConfig.ACKS_CONFIG, "all");
            configProps.put(ProducerConfig.RETRIES_CONFIG, "3");
            configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
            return new DefaultKafkaProducerFactory<>(configProps);
        }

        @Bean
        public KafkaTemplate<String, CourseAssignmentEventAvro> kafkaTemplateWithJsonSchema() {
            return new KafkaTemplate<>(producerFactoryWithJsonSchema());
        }
    }

}
