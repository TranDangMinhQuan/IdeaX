package com.novaid.ideax.dto.startup;

import com.novaid.ideax.enums.FileType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectFileDTO {
    private FileType fileType;
    private String fileUrl;
}