package com.thetiptop.service;

import com.thetiptop.domain.Code;
import com.thetiptop.domain.Participation;
import com.thetiptop.domain.User;
import com.thetiptop.repository.ParticipationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class ParticipationService {

    private final ParticipationRepository participationRepository;
    private final CodeService codeService;

    public ParticipationService(ParticipationRepository participationRepository, CodeService codeService) {
        this.participationRepository = participationRepository;
        this.codeService = codeService;
    }

    @Transactional
    public Participation createParticipation(User user, String codeValue) {
        Code code = codeService.getUsableCode(codeValue);

        participationRepository.findByCodeId(code.getId()).ifPresent(existing -> {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ce code a déjà été utilisé");
        });

        Participation participation = new Participation();
        participation.setUser(user);
        participation.setCode(code);
        participation.setCreatedAt(OffsetDateTime.now());
        participation.setUpdatedAt(participation.getCreatedAt());

        Participation saved = participationRepository.save(participation);
        codeService.markUsed(code, user);
        return saved;
    }

    @Transactional(readOnly = true)
    public List<Participation> getParticipationsForUser(Long userId) {
        return participationRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Page<Participation> getParticipants(Pageable pageable) {
        return participationRepository.findAll(pageable);
    }
}

