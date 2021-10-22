package com.goals.course.manager.service.implementation;

import com.goals.course.manager.configuration.AuthenticatorURL;
import com.goals.course.manager.dto.UserDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserProviderImplTest {

    @Mock
    private RestTemplate mockRestTemplate;
    @Mock
    private AuthenticatorURL mockAuthenticatorURL;
    @InjectMocks
    private UserProviderImpl service;

    @Test
    void findUserById_callUserById() {
        // GIVEN

        // WHEN
        service.findUserById(UUID.fromString("00000000-0000-0000-0000-000000000001"));

        // THEN
        verify(mockAuthenticatorURL).userById(UUID.fromString("00000000-0000-0000-0000-000000000001"));
    }

    @Test
    void findUserById_callGetForObject() {
        // GIVEN
        when(mockAuthenticatorURL.userById(any())).thenReturn("test/url");

        // WHEN
        service.findUserById(null);

        // THEN
        verify(mockRestTemplate).getForObject("test/url", UserDTO.class);
    }

    @Test
    void findUserById_checkResult() {
        // GIVEN
        when(mockAuthenticatorURL.userById(any())).thenReturn("test/url");
        final var userDTO = UserDTO.builder().id(UUID.fromString("00000000-0000-0000-0000-000000000001")).build();
        when(mockRestTemplate.getForObject(anyString(), any())).thenReturn(userDTO);

        // WHEN
        final var result = service.findUserById(null);

        // THEN
        assertThat(result)
                .isPresent()
                .get().isSameAs(userDTO);
    }

}
