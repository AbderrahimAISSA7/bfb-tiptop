package com.thetiptop.api.controller;

import com.thetiptop.api.dto.UserDto;
import com.thetiptop.api.mapper.DtoMapper;
import com.thetiptop.security.AuthenticatedUser;
import com.thetiptop.domain.User;
import com.thetiptop.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    private final DtoMapper mapper = mock(DtoMapper.class);
    private final UserService userService = mock(UserService.class);
    private final UserController controller = new UserController(mapper, userService);

    @Test
    void me_returns_mapped_user() {
        User user = new User();
        user.setEmail("john@example.com");
        AuthenticatedUser principal = Mockito.mock(AuthenticatedUser.class);
        when(principal.getUser()).thenReturn(user);

        UserDto dto = new UserDto();
        dto.setEmail("john@example.com");
        when(mapper.toUserDto(user)).thenReturn(dto);

        UserDto result = controller.me(principal);
        assertEquals("john@example.com", result.getEmail());
    }

    @Test
    void deleteMe_calls_service_and_returns_noContent() {
        AuthenticatedUser principal = Mockito.mock(AuthenticatedUser.class);
        User user = new User();
        user.setId(42L);
        when(principal.getUser()).thenReturn(user);

        ResponseEntity<Void> response = controller.deleteMe(principal);

        verify(userService).deleteAccount(42L);
        assertEquals(204, response.getStatusCode().value());
    }
}