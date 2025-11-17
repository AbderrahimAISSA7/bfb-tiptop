package com.thetiptop.api.controller;

import com.thetiptop.api.dto.ParticipationDto;
import com.thetiptop.api.dto.ParticipationRequest;
import com.thetiptop.api.mapper.DtoMapper;
import com.thetiptop.domain.Participation;
import com.thetiptop.security.AuthenticatedUser;
import com.thetiptop.service.ParticipationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/participations")
@Tag(name = "Participations", description = "Gestion des participations aux concours")
@SecurityRequirement(name = "bearerAuth")
public class ParticipationController {

    private final ParticipationService participationService;
    private final DtoMapper mapper;

    public ParticipationController(ParticipationService participationService, DtoMapper mapper) {
        this.participationService = participationService;
        this.mapper = mapper;
    }

    @PostMapping
    @Operation(summary = "Valider un code", description = "Crée une participation pour le code fourni")
    public ParticipationDto create(@AuthenticationPrincipal AuthenticatedUser principal,
                                   @Valid @RequestBody ParticipationRequest request) {
        Participation participation = participationService.createParticipation(
                principal.getUser(),
                request.getCode());
        return mapper.toParticipationDto(participation);
    }

    @GetMapping("/me")
    @Operation(summary = "Lister mes participations", description = "Retourne les participations de l'utilisateur authentifié")
    public List<ParticipationDto> myParticipations(@AuthenticationPrincipal AuthenticatedUser principal) {
        return participationService.getParticipationsForUser(principal.getId()).stream()
                .map(mapper::toParticipationDto)
                .toList();
    }
}
