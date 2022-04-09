# Course-manager

## This is course-manager microservice of Course application.

### Main technologies:

- Gradle 7.3.3
- Java 17
- Postgres 14
- Spring Boot 2.6.3
- Spring Cloud 2021.0.0
- Netflix Eureka Client
- Flyway 8.0+
- Lombok 1.18+
- Slf4j

---

- Junit 5 + Mockito + Assertj
- TestContainers 1.16
- Spring Cloud Contract 3.0.4

### Responsibilities:

- Creating course, lessons
- Getting course, lessons
- Taking courses (by student)
- Assigning students/instructors to a course
- Getting students by course

## How to build:

**Regular build:** `./gradlew clean build`

**Without tests build:** `./gradlew clean build -x test -x integrationTest -x contractTest`

**Run local in docker
build:** `./gradlew clean build -x test -x integrationTest -x contractTest && docker-compose up --build`

**Push docker image to ECR:**

- aws ecr-public get-login-password --region us-east-1 | docker login --username AWS --password-stdin
  public.ecr.aws/k7s0v3p5
- docker build -t course-manager:0.0.1 .
- docker tag course-manager:0.0.1 public.ecr.aws/k7s0v3p5/course-manager:0.0.1
- docker push public.ecr.aws/k7s0v3p5/course-manager:0.0.1
