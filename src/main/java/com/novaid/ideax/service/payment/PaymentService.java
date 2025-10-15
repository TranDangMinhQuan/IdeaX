package com.novaid.ideax.service.payment;


import com.novaid.ideax.dto.payment.*;
import com.novaid.ideax.entity.payment.Transaction;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface PaymentService {

    PaymentResponseDTO createPayment(Long payerId, CreatePaymentRequestDTO dto);

    PaymentResponseDTO releasePayment(Long payerId, Long paymentId);
    PaymentResponseDTO refundPayment(Long payerId, Long paymentId, RefundRequestDTO dto);
    void createWithdrawRequest(Long accountId, WithdrawRequestDTO dto);
    DepositResponseDTO createDepositRequest(Long accountId, DepositRequestDTO dto, HttpServletRequest request);
    /**
     * Xử lý callback từ cổng thanh toán để xác nhận giao dịch nạp tiền.
     */
    String processVnpayIPN(Map<String, String> vnpayParams);
    WalletResponseDTO getMyWallet(Long accountId);
    Page<TransactionResponseDTO> getMyTransactionHistory(Long accountId, Pageable pageable);
    Page<TransactionResponseDTO> getTransactionHistoryByUserId(Long userId, Pageable pageable);
}