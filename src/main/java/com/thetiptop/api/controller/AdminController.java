package com.thetiptop.api.controller;

import com.thetiptop.api.dto.ParticipantSummaryDto;
import com.thetiptop.api.dto.StatsResponse;
import com.thetiptop.api.mapper.DtoMapper;
import com.thetiptop.service.ParticipationService;
import com.thetiptop.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "Statistiques et pilotage")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final StatsService statsService;
    private final ParticipationService participationService;
    private final DtoMapper mapper;

    public AdminController(StatsService statsService, ParticipationService participationService, DtoMapper mapper) {
        this.statsService = statsService;
        this.participationService = participationService;
        this.mapper = mapper;
    }

    @GetMapping("/stats")
    @Operation(summary = "Statistiques globales", description = "Nombre total/utilisé de codes et répartition des gains")
    public StatsResponse stats() {
        return statsService.buildStats();
    }

    @GetMapping("/participants")
    @Operation(summary = "Lister les participants", description = "Retourne les participations paginées")
    public Page<ParticipantSummaryDto> participants(@RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1));
        return participationService.getParticipants(pageable)
                .map(mapper::toParticipantSummaryDto);
    }
}
