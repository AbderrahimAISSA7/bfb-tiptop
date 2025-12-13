package com.thetiptop.api.controller;

import com.thetiptop.api.dto.AdminCodeDto;
import com.thetiptop.api.dto.AdminUserDto;
import com.thetiptop.api.dto.CreateCodeRequest;
import com.thetiptop.api.dto.ParticipantSummaryDto;
import com.thetiptop.api.dto.PrizeDto;
import com.thetiptop.api.dto.StatsResponse;
import com.thetiptop.api.dto.UpdateCodeStatusRequest;
import com.thetiptop.api.dto.UpdateUserRoleRequest;
import com.thetiptop.api.mapper.DtoMapper;
import com.thetiptop.service.AdminManagementService;
import com.thetiptop.service.ParticipationService;
import com.thetiptop.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "Statistiques et pilotage")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final StatsService statsService;
    private final ParticipationService participationService;
    private final DtoMapper mapper;
    private final AdminManagementService adminManagementService;

    public AdminController(StatsService statsService,
                           ParticipationService participationService,
                           DtoMapper mapper,
                           AdminManagementService adminManagementService) {
        this.statsService = statsService;
        this.participationService = participationService;
        this.mapper = mapper;
        this.adminManagementService = adminManagementService;
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

    @GetMapping("/users")
    @Operation(summary = "Lister les utilisateurs")
    public List<AdminUserDto> users() {
        return adminManagementService.listUsers();
    }

    @PatchMapping("/users/{userId}/role")
    @Operation(summary = "Mettre à jour le rôle d'un utilisateur")
    public AdminUserDto updateUserRole(@PathVariable Long userId, @Valid @RequestBody UpdateUserRoleRequest request) {
        return adminManagementService.updateUserRole(userId, request);
    }

    @DeleteMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer un utilisateur")
    public void deleteUser(@PathVariable Long userId) {
        adminManagementService.deleteUser(userId);
    }

    @GetMapping("/codes")
    @Operation(summary = "Lister les codes")
    public List<AdminCodeDto> codes(@RequestParam(name = "q", defaultValue = "") String query,
                                    @RequestParam(name = "offset", defaultValue = "0") int offset,
                                    @RequestParam(name = "limit", defaultValue = "500") int limit) {
        return adminManagementService.listCodesSlice(query, offset, limit);
    }

    @PostMapping("/codes")
    @Operation(summary = "Créer un nouveau code")
    public AdminCodeDto createCode(@Valid @RequestBody CreateCodeRequest request) {
        return adminManagementService.createCode(request);
    }

    @PatchMapping("/codes/{codeId}/status")
    @Operation(summary = "Mettre à jour le statut d'un code")
    public AdminCodeDto updateCodeStatus(@PathVariable Long codeId,
                                         @Valid @RequestBody UpdateCodeStatusRequest request) {
        return adminManagementService.updateCodeStatus(codeId, request);
    }

    @GetMapping("/prizes")
    @Operation(summary = "Lister les lots disponibles")
    public List<PrizeDto> prizes() {
        return adminManagementService.listPrizes();
    }

    @GetMapping(value = "/codes/export", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Exporter les codes (JSON)")
    public void exportCodesJson(HttpServletResponse response,
                                @RequestParam(name = "q", defaultValue = "") String query,
                                @RequestParam(name = "status", required = false) String status) throws IOException {
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        adminManagementService.writeCodesJson(response.getOutputStream(), query, status);
    }
}
