package com.novaid.ideax.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ngành dự án")
public enum Category {
    FINTECH,
    HEALTHCARE,
    EDUCATION,
    ECOMMERCE,
    AI,
    BLOCKCHAIN,
    GREEN_TECH,
    AGRICULTURE,
    OTHER
}
