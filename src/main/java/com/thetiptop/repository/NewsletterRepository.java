package com.thetiptop.repository;

import com.thetiptop.domain.Newsletter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NewsletterRepository extends JpaRepository<Newsletter, Long> {

    Optional<Newsletter> findByEmail(String email);
}

