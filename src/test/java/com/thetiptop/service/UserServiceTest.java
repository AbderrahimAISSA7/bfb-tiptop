package com.thetiptop.service;

import com.thetiptop.api.dto.RegisterRequest;
import com.thetiptop.domain.User;
import com.thetiptop.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private RegisterRequest request;

    @BeforeEach
    void setup() {
        request = new RegisterRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john@example.com");
        request.setPassword("secret");
    }

    @Test
    void register_should_save_user_when_email_free() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("secret")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User saved = userService.register(request);

        assertEquals("john@example.com", saved.getEmail());
        assertEquals("hashed", saved.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_should_throw_on_duplicate_email() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(new User()));
        assertThrows(ResponseStatusException.class, () -> userService.register(request));
    }

    @Test
    void deleteAccount_should_delete_existing_user() {
        when(userRepository.existsById(1L)).thenReturn(true);
        userService.deleteAccount(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteAccount_should_throw_if_not_found() {
        when(userRepository.existsById(99L)).thenReturn(false);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> userService.deleteAccount(99L));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }
}