package com.thetiptop.repository;

import com.thetiptop.domain.Code;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CodeRepository extends JpaRepository<Code, Long> {

    Optional<Code> findByCode(String code);

    List<Code> findByStatus(String status);

    long countByStatus(String status);

    Page<Code> findByCodeContainingIgnoreCase(String code, Pageable pageable);

    Page<Code> findByStatus(String status, Pageable pageable);

    Page<Code> findByCodeContainingIgnoreCaseAndStatus(String code, String status, Pageable pageable);
}
