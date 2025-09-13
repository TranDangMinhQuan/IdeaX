package com.novaid.ideax.repository.auth;

import com.novaid.ideax.entity.auth.InvestorProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvestorProfileRepository extends JpaRepository<InvestorProfile, Long> {
    Optional<InvestorProfile> findByAccountId(Long accountId);
}