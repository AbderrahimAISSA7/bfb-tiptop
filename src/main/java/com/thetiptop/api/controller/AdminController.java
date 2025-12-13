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
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;

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
    @Operation(summary = "Statistiques globales", description = "Nombre total/utilisÃ© de codes et rÃ©partition des gains")
    public StatsResponse stats() {
        return statsService.buildStats();
    }

    @GetMapping("/participants")
    @Operation(summary = "Lister les participants", description = "Retourne les participations paginÃ©es")
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
    @Operation(summary = "Mettre Ã  jour le rÃ´le d'un utilisateur")
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
    @Operation(summary = "Lister les codes (fenêtrage offset/limit)")
    public List<AdminCodeDto> codes(@RequestParam(value = "q", defaultValue = "") String query,
                                    @RequestParam(value = "offset", defaultValue = "0") int offset,
                                    @RequestParam(value = "limit", defaultValue = "500") int limit) {
        return adminManagementService.listCodesSlice(query, offset, limit);
    }

    @GetMapping("/codes/export")
    @Operation(summary = "Exporter tous les codes (JSON streaming)")
    public ResponseEntity<StreamingResponseBody> exportCodes(@RequestParam(value = "q", defaultValue = "") String query,
                                                             @RequestParam(value = "status", required = false) String status) {
        StreamingResponseBody body = outputStream -> adminManagementService.writeCodesJson(outputStream, query, status);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .header("Content-Disposition", "attachment; filename=\"codes.json\"")
                .body(body);
    }

    @PostMapping("/codes")
    @Operation(summary = "CrÃ©er un nouveau code")
    public AdminCodeDto createCode(@Valid @RequestBody CreateCodeRequest request) {
        return adminManagementService.createCode(request);
    }

    @PatchMapping("/codes/{codeId}/status")
    @Operation(summary = "Mettre Ã  jour le statut d'un code")
    public AdminCodeDto updateCodeStatus(@PathVariable Long codeId,
                                         @Valid @RequestBody UpdateCodeStatusRequest request) {
        return adminManagementService.updateCodeStatus(codeId, request);
    }

    @GetMapping("/prizes")
    @Operation(summary = "Lister les lots disponibles")
    public List<PrizeDto> prizes() {
        return adminManagementService.listPrizes();
    }
}


