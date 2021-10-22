package com.goals.course.manager;

import com.goals.course.manager.dao.entity.Course;
import com.goals.course.manager.dao.entity.CourseInstructor;
import com.goals.course.manager.dao.entity.Instructor;
import com.goals.course.manager.dao.enums.Roles;
import com.goals.course.manager.dao.repository.CourseInstructorRepository;
import com.goals.course.manager.dao.repository.CourseRepository;
import com.goals.course.manager.dto.RoleDTO;
import com.goals.course.manager.dto.UserDTO;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.ClassRule;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ActiveProfiles("contract-test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ContextConfiguration(initializers = ContractVerifierBaseTestClass.Initializer.class)
@DirtiesContext
@AutoConfigureMessageVerifier
@AutoConfigureMockMvc
public class ContractVerifierBaseTestClass {

    @ClassRule
    public static final PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer<>("postgres:14.0")
            .withDatabaseName("course_manager")
            .withUsername("sa")
            .withPassword("sa");
    @Autowired
    private MockMvc mockMvc;
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
        RestAssuredMockMvc.mockMvc(mockMvc);
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
                .collect(Collectors.toList());
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


    static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            postgreSQLContainer.start();
            TestPropertyValues.of(
                    "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                    "spring.datasource.password=" + postgreSQLContainer.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

}
