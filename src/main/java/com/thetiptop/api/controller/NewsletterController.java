package com.thetiptop.api.controller;

import com.thetiptop.api.dto.NewsletterDto;
import com.thetiptop.api.dto.NewsletterRequest;
import com.thetiptop.api.mapper.DtoMapper;
import com.thetiptop.domain.Newsletter;
import com.thetiptop.service.NewsletterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/newsletters")
@Tag(name = "Newsletters", description = "Gestion des inscriptions newsletters")
public class NewsletterController {

    private final NewsletterService newsletterService;
    private final DtoMapper mapper;

    public NewsletterController(NewsletterService newsletterService, DtoMapper mapper) {
        this.newsletterService = newsletterService;
        this.mapper = mapper;
    }

    @PostMapping
    @Operation(summary = "Inscrire un email", description = "Ajoute une adresse email à la newsletter")
    public NewsletterDto subscribe(@Valid @RequestBody NewsletterRequest request) {
        Newsletter newsletter = newsletterService.subscribe(request.getEmail().toLowerCase());
        return mapper.toNewsletterDto(newsletter);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Désinscription", description = "Supprime une inscription newsletter")
    public void unsubscribe(@PathVariable Long id) {
        newsletterService.unsubscribe(id);
    }
}
