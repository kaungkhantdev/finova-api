package com.finova.api.repository;

import com.finova.api.entity.OtpCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpCodeRepository extends JpaRepository<OtpCode, Long> {
    Optional<OtpCode> findTopByEmailAndUsedFalseOrderByCreatedAtDesc(String email);
}
