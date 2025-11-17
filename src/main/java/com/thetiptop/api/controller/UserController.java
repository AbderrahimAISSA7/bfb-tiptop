package com.thetiptop.api.controller;

import com.thetiptop.api.dto.UserDto;
import com.thetiptop.api.mapper.DtoMapper;
import com.thetiptop.security.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Gestion du profil utilisateur")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final DtoMapper mapper;

    public UserController(DtoMapper mapper) {
        this.mapper = mapper;
    }

    @GetMapping("/me")
    @Operation(summary = "Profil courant", description = "Retourne les informations de l'utilisateur authentifié")
    public UserDto me(@AuthenticationPrincipal AuthenticatedUser principal) {
        return mapper.toUserDto(principal.getUser());
    }
}
