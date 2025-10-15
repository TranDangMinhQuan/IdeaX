package com.novaid.ideax.enums;

public enum TransactionType {
    DEPOSIT,
    WITHDRAW,
    PROJECT_PAYMENT, // Investor thanh toán cho dự án (trừ tiền)
    PAYMENT_RELEASE, // Startup nhận tiền giải ngân (cộng tiền)
    PAYMENT_REFUND   // Investor nhận lại tiền hoàn (cộng tiền)
}