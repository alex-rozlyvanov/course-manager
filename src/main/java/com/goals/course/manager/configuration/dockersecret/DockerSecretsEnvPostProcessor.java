package com.goals.course.manager.configuration.dockersecret;

import com.goals.course.manager.configuration.dockersecret.exception.DockerSecretPostProcessorException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.util.CollectionUtils;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;

public class DockerSecretsEnvPostProcessor implements EnvironmentPostProcessor, Ordered {


    public void postProcessEnvironment(final ConfigurableEnvironment env,
                                       final SpringApplication application) {
        final var secretsDirectory = Paths.get("/run/secrets");

        if (secretsDirectory.toFile().exists()) {
            addDockerSecretsPropertySource(env, secretsDirectory);
        }
    }

    private void addDockerSecretsPropertySource(final ConfigurableEnvironment env,
                                                final Path secretsDirectory) {
        final var dockerSecretsMap = getDockerSecretsMap(secretsDirectory);

        if (!CollectionUtils.isEmpty(dockerSecretsMap)) {
            final var mps = new MapPropertySource("Docker", dockerSecretsMap);
            env.getPropertySources().addLast(mps);
        }
    }

    protected Map<String, Object> getDockerSecretsMap(final Path secretsDirectory) {
        try (final var list = Files.list(secretsDirectory)) {
            return list
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toMap(filePath -> filePath.toFile().getName(), this::readSecret));
        } catch (IOException e) {
            throw new DockerSecretPostProcessorException(e);
        }
    }

    private String readSecret(final Path filePath) {
        final var in = filePath.toFile();
        try {
            final var content = FileCopyUtils.copyToByteArray(in);
            return new String(content, StandardCharsets.UTF_8).trim();
        } catch (IOException e) {
            throw new DockerSecretPostProcessorException(e);
        }
    }


    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

}
