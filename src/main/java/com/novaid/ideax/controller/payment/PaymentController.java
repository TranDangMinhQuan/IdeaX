package com.novaid.ideax.controller.payment;

import com.novaid.ideax.dto.payment.*;
import com.novaid.ideax.entity.auth.Account;

import com.novaid.ideax.service.payment.PaymentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@SecurityRequirement(name = "api")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/wallet/me")
    @PreAuthorize("hasAnyRole('INVESTOR', 'START_UP')")
    public ResponseEntity<WalletResponseDTO> getMyWallet(Authentication authentication) {
        Account account = (Account) authentication.getPrincipal();
        return ResponseEntity.ok(paymentService.getMyWallet(account.getId()));
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('INVESTOR')")
    public ResponseEntity<PaymentResponseDTO> createPayment(
            Authentication authentication,
            @Valid @RequestBody CreatePaymentRequestDTO dto) {
        Account payer = (Account) authentication.getPrincipal();
        return ResponseEntity.ok(paymentService.createPayment(payer.getId(), dto));
    }

    @PostMapping("/{paymentId}/release")
    @PreAuthorize("hasRole('INVESTOR')")
    public ResponseEntity<PaymentResponseDTO> releasePayment(
            Authentication authentication,
            @PathVariable Long paymentId) {
        Account payer = (Account) authentication.getPrincipal();
        return ResponseEntity.ok(paymentService.releasePayment(payer.getId(), paymentId));
    }

    @PostMapping("/{paymentId}/refund")
    @PreAuthorize("hasRole('INVESTOR')")
    public ResponseEntity<PaymentResponseDTO> refundPayment(
            Authentication authentication,
            @PathVariable Long paymentId,
            @Valid @RequestBody RefundRequestDTO dto) {
        Account payer = (Account) authentication.getPrincipal();
        return ResponseEntity.ok(paymentService.refundPayment(payer.getId(), paymentId, dto));
    }
    @GetMapping("/transactions/history")
    @PreAuthorize("hasAnyRole('INVESTOR', 'START_UP')")
    public ResponseEntity<Page<TransactionResponseDTO>> getMyTransactionHistory(
            Authentication authentication,
            Pageable pageable) {
        Account account = (Account) authentication.getPrincipal();
        Page<TransactionResponseDTO> historyPage = paymentService.getMyTransactionHistory(account.getId(), pageable);
        return ResponseEntity.ok(historyPage);
    }
    @GetMapping("/transactions/history/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<TransactionResponseDTO>> getTransactionHistoryByUserId(
            @PathVariable Long userId,
            Pageable pageable) {
        Page<TransactionResponseDTO> historyPage = paymentService.getTransactionHistoryByUserId(userId, pageable);
        return ResponseEntity.ok(historyPage);
    }
    @GetMapping("/webhook/vnpay-return")
    public ResponseEntity<Void> handleVnpayReturn(HttpServletRequest request) {
        // Chuyển hướng người dùng về trang kết quả trên frontend
        // Ví dụ: return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("https://your-frontend.com/payment-result")).build();
        return ResponseEntity.ok().build(); // Hoặc trả về một trang HTML thông báo đơn giản
    }
    @GetMapping("/webhook/vnpay-ipn")
    public ResponseEntity<String> handleVnpayIPN(@RequestParam Map<String, String> allParams) {
        String result = paymentService.processVnpayIPN(allParams);
        return ResponseEntity.ok(result);
    }
    @PostMapping("/wallet/withdraw")
    @PreAuthorize("hasAnyRole('INVESTOR', 'START_UP')")
    public ResponseEntity<String> createWithdrawRequest(
            Authentication authentication,
            @Valid @RequestBody WithdrawRequestDTO dto) {
        Account account = (Account) authentication.getPrincipal();
        paymentService.createWithdrawRequest(account.getId(), dto);
        return ResponseEntity.ok("Withdraw request created successfully and is pending approval.");
    }
    @PostMapping("/wallet/deposit")
    @PreAuthorize("hasAnyRole('INVESTOR', 'START_UP')")
    public ResponseEntity<DepositResponseDTO> createDepositRequest(
            Authentication authentication,
            @Valid @RequestBody DepositRequestDTO dto,
            HttpServletRequest request) { // Thêm HttpServletRequest
        Account account = (Account) authentication.getPrincipal();
        // Truyền request vào service
        return ResponseEntity.ok(paymentService.createDepositRequest(account.getId(), dto, request));
    }
}