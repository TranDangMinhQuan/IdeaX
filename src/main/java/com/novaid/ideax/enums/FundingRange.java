package com.novaid.ideax.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Khoảng vốn cần gọi")
public enum FundingRange {
    UNDER_50K,
    FROM_50K_TO_200K,
    FROM_200K_TO_1M,
    OVER_1M
}
