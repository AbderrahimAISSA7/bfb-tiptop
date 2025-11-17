package com.thetiptop.repository;

import com.thetiptop.domain.Code;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CodeRepository extends JpaRepository<Code, Long> {

    Optional<Code> findByCode(String code);

    List<Code> findByStatus(String status);

    long countByStatus(String status);
}
