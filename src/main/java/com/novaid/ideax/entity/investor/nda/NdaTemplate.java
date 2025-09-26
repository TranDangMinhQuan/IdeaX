package com.novaid.ideax.entity.investor.nda;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "nda_template")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NdaTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileUrl;   // đường dẫn file NDA (admin upload 1 lần)

    private LocalDateTime uploadedAt;
}