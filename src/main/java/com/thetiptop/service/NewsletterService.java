package com.thetiptop.service;

import com.thetiptop.domain.Newsletter;
import com.thetiptop.repository.NewsletterRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;

@Service
public class NewsletterService {

    private final NewsletterRepository newsletterRepository;

    public NewsletterService(NewsletterRepository newsletterRepository) {
        this.newsletterRepository = newsletterRepository;
    }

    @Transactional
    public Newsletter subscribe(String email) {
        String normalizedEmail = email.toLowerCase();
        return newsletterRepository.findByEmail(normalizedEmail)
                .orElseGet(() -> {
                    Newsletter newsletter = new Newsletter();
                    newsletter.setEmail(normalizedEmail);
                    newsletter.setCreatedAt(OffsetDateTime.now());
                    newsletter.setUpdatedAt(newsletter.getCreatedAt());
                    return newsletterRepository.save(newsletter);
                });
    }

    @Transactional
    public void unsubscribe(Long id) {
        Newsletter entity = newsletterRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Newsletter entry not found"));
        newsletterRepository.delete(entity);
    }
}
