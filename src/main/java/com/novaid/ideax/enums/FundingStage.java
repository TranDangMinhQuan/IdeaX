package com.novaid.ideax.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Giai đoạn gọi vốn")
public enum FundingStage {
    IDEA,
    SEED,
    SERIES_A,
    SERIES_B,
    SERIES_C,
    IPO
}
