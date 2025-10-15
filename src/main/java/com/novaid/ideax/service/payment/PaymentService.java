package com.novaid.ideax.service.payment;


import com.novaid.ideax.dto.payment.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentService {

    PaymentResponseDTO createPayment(Long payerId, CreatePaymentRequestDTO dto);

    PaymentResponseDTO releasePayment(Long payerId, Long paymentId);

    PaymentResponseDTO refundPayment(Long payerId, Long paymentId, RefundRequestDTO dto);
    DepositResponseDTO createDepositRequest(Long accountId, DepositRequestDTO dto);

    /**
     * Xử lý callback từ cổng thanh toán để xác nhận giao dịch nạp tiền.
     */
    void processPaymentCallback(String transactionId, String status); // Các tham số sẽ tùy thuộc vào cổng thanh toán
    WalletResponseDTO getMyWallet(Long accountId);
    Page<TransactionResponseDTO> getMyTransactionHistory(Long accountId, Pageable pageable);
    Page<TransactionResponseDTO> getTransactionHistoryByUserId(Long userId, Pageable pageable);
}