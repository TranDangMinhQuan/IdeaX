package com.novaid.ideax.repository.auth;

import com.novaid.ideax.entity.auth.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthenticationRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByEmail(String email);
    Account findAccountByEmail(String email);
    boolean existsByEmail(String email);
}
