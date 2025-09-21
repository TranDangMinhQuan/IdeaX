package com.novaid.ideax.dto.startup;

import com.novaid.ideax.enums.MilestoneStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MilestoneRequestDTO {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Due date is required")
    private LocalDate dueDate;

    private MilestoneStatus status = MilestoneStatus.PENDING;
}

