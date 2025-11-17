package com.thetiptop.service;

import com.thetiptop.domain.Code;
import com.thetiptop.domain.Participation;
import com.thetiptop.domain.Prize;
import com.thetiptop.domain.User;
import com.thetiptop.repository.CodeRepository;
import com.thetiptop.repository.ParticipationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParticipationServiceTest {

    @Mock
    private ParticipationRepository participationRepository;

    @Mock
    private CodeRepository codeRepository;

    private ParticipationService participationService;
    private User user;
    private Code code;

    @BeforeEach
    void setUp() {
        CodeService codeService = new CodeService(codeRepository);
        participationService = new ParticipationService(participationRepository, codeService);

        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setRole("USER");

        code = buildCode();
    }

    @Test
    void createParticipation_marksCodeAsUsed() {
        when(codeRepository.findByCode("ABC123")).thenReturn(Optional.of(code));
        when(participationRepository.findByCodeId(code.getId())).thenReturn(Optional.empty());
        when(participationRepository.save(any(Participation.class))).thenAnswer(invocation -> {
            Participation toSave = invocation.getArgument(0);
            toSave.setId(99L);
            return toSave;
        });
        when(codeRepository.save(any(Code.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Participation participation = participationService.createParticipation(user, "ABC123");

        assertEquals(99L, participation.getId());
        assertEquals(CodeService.STATUS_USED, code.getStatus());
        assertThat(code.getValidatedBy()).isEqualTo(user);
        verify(participationRepository).save(any(Participation.class));
        verify(codeRepository).save(code);
    }

    @Test
    void createParticipation_rejectsAlreadyUsedCode() {
        when(codeRepository.findByCode("ABC123")).thenReturn(Optional.of(code));
        when(participationRepository.findByCodeId(code.getId())).thenReturn(Optional.of(new Participation()));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> participationService.createParticipation(user, "ABC123"));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exception.getReason()).contains("déjà");
    }

    @Test
    void createParticipation_rejectsExpiredCode() {
        code.setExpirationDate(OffsetDateTime.now().minusDays(1));
        when(codeRepository.findByCode("ABC123")).thenReturn(Optional.of(code));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> participationService.createParticipation(user, "ABC123"));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exception.getReason()).contains("expir");
    }

    private Code buildCode() {
        Code entity = new Code();
        entity.setId(5L);
        entity.setCode("ABC123");
        entity.setStatus(CodeService.STATUS_NEW);
        entity.setExpirationDate(OffsetDateTime.now().plusDays(5));
        Prize prize = new Prize();
        prize.setId(2L);
        prize.setName("Pack");
        entity.setPrize(prize);
        return entity;
    }
}
