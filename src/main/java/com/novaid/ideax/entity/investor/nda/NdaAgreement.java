package com.novaid.ideax.entity.investor.nda;


import com.novaid.ideax.entity.auth.Account;
import com.novaid.ideax.entity.investor.meet.MeetingRoom;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "nda_agreement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NdaAgreement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean signed;
    private LocalDateTime signedAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Account user;

    @ManyToOne
    @JoinColumn(name = "nda_template_id", nullable = false)
    private NdaTemplate ndaTemplate;
}