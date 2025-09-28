package com.novaid.ideax.entity.startup.payment;

import com.novaid.ideax.entity.auth.Account;
import com.novaid.ideax.entity.auth.StartupProfile;
import com.novaid.ideax.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "investor_id", nullable = false)
    private Account investor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "startup_id", nullable = false)
    private StartupProfile startup;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status; // PENDING, APPROVED, RELEASED

    @Column(nullable = false)
    private BigDecimal fee; // 5-10% phí app

    @Column(nullable = false)
    private BigDecimal netAmount; // amount - fee

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
