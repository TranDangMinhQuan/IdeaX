package com.novaid.ideax.repository.auth;

import com.novaid.ideax.entity.auth.Account;
import com.novaid.ideax.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByRole(Role role);
    List<Account> findAllByRole(Role role);
}
