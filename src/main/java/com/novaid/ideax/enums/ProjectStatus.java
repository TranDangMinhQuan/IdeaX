package com.novaid.ideax.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Trạng thái dự án")
public enum ProjectStatus {
    DRAFT,
    PUBLISHED,
    APPROVED,
    REJECTED
}
