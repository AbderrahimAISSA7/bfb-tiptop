package com.thetiptop.repository;

import com.thetiptop.domain.Participation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    List<Participation> findByUserId(Long userId);

    Optional<Participation> findByCodeId(Long codeId);
}

