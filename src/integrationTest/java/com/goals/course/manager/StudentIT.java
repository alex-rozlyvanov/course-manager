package com.goals.course.manager;

import com.goals.course.avro.CourseAssignmentEventAvro;
import com.goals.course.manager.dao.entity.Course;
import com.goals.course.manager.dao.enums.Roles;
import com.goals.course.manager.dao.repository.CourseRepository;
import com.goals.course.manager.dto.AssignStudentToCourseRequest;
import com.goals.course.manager.dto.RoleDTO;
import com.goals.course.manager.dto.UserDTO;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.*;
import java.util.stream.IntStream;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "app.microservice.authorization.url=http://localhost:${wiremock.server.port}"
        }
)
@ActiveProfiles("integration")
@AutoConfigureWebTestClient(timeout = "PT2H")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureWireMock(port = 0, stubs = "file:libs/contracts/*.json")
@ContextConfiguration(initializers = {
        StudentIT.TestContainersInitializer.class
})
//@AutoConfigureStubRunner(
//        ids = {"com.goals.course:course-authenticator:+:8180"}
//        , stubsMode = StubRunnerProperties.StubsMode.LOCAL)
class StudentIT {

    private static final PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer<>("postgres:14.0")
            .withDatabaseName("course_manager")
            .withUsername("sa")
            .withPassword("sa");
    private static final DockerImageName KAFKA_IMAGE_NAME = DockerImageName.parse("confluentinc/cp-kafka:7.0.1")
            .asCompatibleSubstituteFor("confluentinc/cp-kafka");
    private static final KafkaContainer kafka = new KafkaContainer(KAFKA_IMAGE_NAME);

    private final String TAKE_COURSE_URI = "/api/manager/courses/%s/take";
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private CourseRepository courseRepository;
    @Value("${app.jwt.access.secret}")
    private String accessTokenSecret;
    @Value("${app.jwt.access.expiration}")
    private Duration accessTokenExpiration;
    @Value("${app.jwt.issuer}")
    private String jwtIssuer;
    @Value("${app.student.maxNumberOfCourses}")
    private Integer maxNumberOfCoursesStudentCanTake;

    @Test
    void studentCanTakeUpTo5CoursesAtTheSameTime() {
        // GIVEN
        final var userDTO = buildUserDTO();
        final var accessToken = generateAccessToken(userDTO);
        final var request = new AssignStudentToCourseRequest(userDTO.getId());

        // WHEN
        IntStream.rangeClosed(1, maxNumberOfCoursesStudentCanTake)
                .forEach((i) -> {
                    final var course = createCourse("test course " + i);
                    post(TAKE_COURSE_URI.formatted(course.getId()), request, accessToken)
                            .expectStatus().isOk();
                });

        // THEN
        final var courseId = createCourse("test course " + maxNumberOfCoursesStudentCanTake + 1).getId();
        post(TAKE_COURSE_URI.formatted(courseId), request, accessToken)
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error")
                .isEqualTo("Student cannot take more courses. Maximum is " + maxNumberOfCoursesStudentCanTake);
    }

    private UserDTO buildUserDTO() {
        final var userId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        final var roles = List.of(RoleDTO.builder().title(Roles.STUDENT.name()).build());
        return UserDTO.builder().id(userId).username("user+student").roles(roles).build();
    }

    private Course createCourse(final String title) {
        final var course = new Course().setTitle(title);

        return courseRepository.save(course);
    }

    private WebTestClient.ResponseSpec post(final String uri, final Object body, final String accessToken) {
        return webTestClient.post().uri(uri)
                .header(AUTHORIZATION, "Bearer %s".formatted(accessToken))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange();
    }

    public String generateAccessToken(final UserDTO user) {
        final var claims = buildClaims(user);

        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuer(jwtIssuer)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration.toMillis()))
                .signWith(SignatureAlgorithm.HS256, accessTokenSecret)
                .addClaims(claims)
                .compact();
    }

    private Map<String, Object> buildClaims(final UserDTO user) {
        final var roles = user.getRoles();
        return Map.of("userId", user.getId().toString(), "roles", roles);
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
