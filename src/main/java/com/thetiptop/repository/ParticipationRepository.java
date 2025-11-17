package com.thetiptop.repository;

import com.thetiptop.domain.Participation;
import com.thetiptop.repository.projection.PrizeCountProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    List<Participation> findByUserId(Long userId);

    Optional<Participation> findByCodeId(Long codeId);

    @Query("SELECT p.code.prize.name AS prizeName, COUNT(p) AS prizeCount FROM Participation p GROUP BY p.code.prize.name")
    List<PrizeCountProjection> countByPrize();
}
