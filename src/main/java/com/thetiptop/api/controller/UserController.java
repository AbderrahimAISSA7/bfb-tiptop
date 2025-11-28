package com.thetiptop.api.controller;

import com.thetiptop.api.dto.UserDto;
import com.thetiptop.api.mapper.DtoMapper;
import com.thetiptop.security.AuthenticatedUser;
import com.thetiptop.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Gestion du profil utilisateur")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final DtoMapper mapper;
    private final UserService userService;

    public UserController(DtoMapper mapper, UserService userService) {
        this.mapper = mapper;
        this.userService = userService;
    }

    @GetMapping("/me")
    @Operation(summary = "Profil courant", description = "Retourne les informations de l'utilisateur authentifié")
    public UserDto me(@AuthenticationPrincipal AuthenticatedUser principal) {
        return mapper.toUserDto(principal.getUser());
    }

    @DeleteMapping("/me")
    @Operation(summary = "Supprimer le compte", description = "Supprime l'utilisateur courant")
    public ResponseEntity<Void> deleteMe(@AuthenticationPrincipal AuthenticatedUser principal) {
        userService.deleteAccount(principal.getUser().getId());
        return ResponseEntity.noContent().build();
    }
}