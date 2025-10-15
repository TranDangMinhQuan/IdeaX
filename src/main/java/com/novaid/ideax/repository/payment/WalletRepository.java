package com.novaid.ideax.repository.payment;

import com.novaid.ideax.entity.payment.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByAccountId(Long accountId);
}