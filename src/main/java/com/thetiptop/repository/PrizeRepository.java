package com.thetiptop.repository;

import com.thetiptop.domain.Prize;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PrizeRepository extends JpaRepository<Prize, Long> {

    Optional<Prize> findByName(String name);
}

