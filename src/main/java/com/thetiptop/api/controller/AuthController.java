package com.thetiptop.api.controller;

import com.thetiptop.api.dto.AuthResponse;
import com.thetiptop.api.dto.LoginRequest;
import com.thetiptop.api.dto.RegisterRequest;
import com.thetiptop.api.mapper.DtoMapper;
import com.thetiptop.domain.User;
import com.thetiptop.security.JwtService;
import com.thetiptop.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Inscription et authentification")
public class AuthController {

    private final UserService userService;
    private final DtoMapper mapper;
    private final JwtService jwtService;

    public AuthController(UserService userService, DtoMapper mapper, JwtService jwtService) {
        this.userService = userService;
        this.mapper = mapper;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    @Operation(summary = "Créer un compte", description = "Inscrit un nouvel utilisateur et renvoie un token JWT")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(request);
        return buildResponse(user);
    }

    @PostMapping("/login")
    @Operation(summary = "Se connecter", description = "Authentifie un utilisateur et renvoie un token JWT")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        User user = userService.authenticate(request.getEmail(), request.getPassword());
        return buildResponse(user);
    }

    private AuthResponse buildResponse(User user) {
        String token = jwtService.generateToken(user.getEmail(), user.getRole());
        return new AuthResponse(token, mapper.toUserDto(user));
    }
}
