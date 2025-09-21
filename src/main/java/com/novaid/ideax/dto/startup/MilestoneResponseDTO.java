package com.novaid.ideax.dto.startup;

import com.novaid.ideax.enums.MilestoneStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MilestoneResponseDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDate dueDate;
    private MilestoneStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}