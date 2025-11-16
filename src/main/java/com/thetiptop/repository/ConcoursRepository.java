package com.thetiptop.repository;

import com.thetiptop.domain.Concours;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConcoursRepository extends JpaRepository<Concours, Long> {

    Optional<Concours> findTopByOrderByStartDateDesc();
}

