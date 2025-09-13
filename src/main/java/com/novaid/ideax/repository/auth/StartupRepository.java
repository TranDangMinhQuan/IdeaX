package com.novaid.ideax.repository.auth;

import com.novaid.ideax.entity.auth.Account;
import com.novaid.ideax.entity.auth.StartupProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StartupRepository extends JpaRepository<StartupProfile, Long> {
    Optional<StartupProfile> findByAccount(Account account);
}
