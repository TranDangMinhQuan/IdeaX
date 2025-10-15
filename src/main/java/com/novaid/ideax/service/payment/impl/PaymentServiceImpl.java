package com.novaid.ideax.service.payment.impl;

import com.novaid.ideax.dto.payment.*;
import com.novaid.ideax.entity.auth.Account;
import com.novaid.ideax.entity.payment.ProjectPayment;
import com.novaid.ideax.entity.payment.Transaction;
import com.novaid.ideax.entity.payment.Wallet;
import com.novaid.ideax.entity.startup.project.Project;

import com.novaid.ideax.enums.PaymentStatus;
import com.novaid.ideax.enums.TransactionStatus;
import com.novaid.ideax.enums.TransactionType;
import com.novaid.ideax.exception.InsufficientFundsException;
import com.novaid.ideax.repository.auth.AccountRepository;
import com.novaid.ideax.repository.payment.ProjectPaymentRepository;
import com.novaid.ideax.repository.payment.TransactionRepository;
import com.novaid.ideax.repository.payment.WalletRepository;
import com.novaid.ideax.repository.startup.project.ProjectRepository;
import com.novaid.ideax.service.payment.PaymentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final AccountRepository accountRepository;
    private final ProjectRepository projectRepository;
    private final WalletRepository walletRepository;
    private final ProjectPaymentRepository paymentRepository;
    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public PaymentResponseDTO createPayment(Long payerId, CreatePaymentRequestDTO dto) {
        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));

        if (project.getFundingAmount() == null || project.getFundingAmount().compareTo(dto.getAmount()) != 0) {
            throw new IllegalArgumentException("Payment amount must match the project's required funding.");
        }

        Account payer = accountRepository.findById(payerId)
                .orElseThrow(() -> new EntityNotFoundException("Payer account not found"));

        Wallet payerWallet = walletRepository.findByAccountId(payerId)
                .orElseThrow(() -> new EntityNotFoundException("Payer wallet not found"));

        if (payerWallet.getBalance().compareTo(dto.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds in your wallet.");
        }

        payerWallet.setBalance(payerWallet.getBalance().subtract(dto.getAmount()));
        walletRepository.save(payerWallet);

        ProjectPayment payment = ProjectPayment.builder()
                .payer(payer)
                .project(project)
                .amount(dto.getAmount())
                .status(PaymentStatus.PENDING)
                .build();
        ProjectPayment savedPayment = paymentRepository.save(payment);

        createTransaction(payerWallet, dto.getAmount().negate(), TransactionType.PROJECT_PAYMENT, savedPayment.getId());

        return mapToResponse(savedPayment);
    }

    @Override
    @Transactional
    public PaymentResponseDTO releasePayment(Long payerId, Long paymentId) {
        ProjectPayment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("Payment record not found"));

        if (!payment.getPayer().getId().equals(payerId)) {
            throw new AccessDeniedException("You are not authorized to release this payment.");
        }
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException("Payment is not in PENDING state.");
        }

        Account recipient = payment.getProject().getStartup();
        Wallet recipientWallet = walletRepository.findByAccountId(recipient.getId())
                .orElseThrow(() -> new EntityNotFoundException("Recipient (Startup) wallet not found"));

        recipientWallet.setBalance(recipientWallet.getBalance().add(payment.getAmount()));
        walletRepository.save(recipientWallet);

        payment.setStatus(PaymentStatus.RELEASED);
        payment.setUpdatedAt(LocalDateTime.now());
        ProjectPayment updatedPayment = paymentRepository.save(payment);

        createTransaction(recipientWallet, payment.getAmount(), TransactionType.PAYMENT_RELEASE, paymentId);

        return mapToResponse(updatedPayment);
    }

    @Override
    @Transactional
    public PaymentResponseDTO refundPayment(Long payerId, Long paymentId, RefundRequestDTO dto) {
        ProjectPayment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("Payment record not found"));

        if (!payment.getPayer().getId().equals(payerId)) {
            throw new AccessDeniedException("You are not authorized to refund this payment.");
        }
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException("Only PENDING payments can be refunded.");
        }

        Wallet payerWallet = walletRepository.findByAccountId(payerId)
                .orElseThrow(() -> new EntityNotFoundException("Payer wallet not found"));

        payerWallet.setBalance(payerWallet.getBalance().add(payment.getAmount()));
        walletRepository.save(payerWallet);

        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setReason(dto.getReason());
        payment.setUpdatedAt(LocalDateTime.now());
        ProjectPayment updatedPayment = paymentRepository.save(payment);

        createTransaction(payerWallet, payment.getAmount(), TransactionType.PAYMENT_REFUND, paymentId);

        return mapToResponse(updatedPayment);
    }

    @Override
    public WalletResponseDTO getMyWallet(Long accountId) {
        Wallet wallet = walletRepository.findByAccountId(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found for this account"));

        return WalletResponseDTO.builder()
                .accountId(accountId)
                .email(wallet.getAccount().getEmail())
                .balance(wallet.getBalance())
                .build();
    }

    private void createTransaction(Wallet wallet, BigDecimal amount, TransactionType type, Long paymentId) {
        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .amount(amount)
                .type(type)
                .status(TransactionStatus.SUCCESS)
                .paymentId(paymentId)
                .build();
        transactionRepository.save(transaction);
    }
    @Override
    @Transactional(readOnly = true)
    public Page<TransactionResponseDTO> getTransactionHistoryByUserId(Long userId, Pageable pageable) {
        // Tìm ví dựa trên userId được cung cấp, thay vì người dùng đang đăng nhập
        Wallet wallet = walletRepository.findByAccountId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found for account with ID: " + userId));

        // Logic còn lại hoàn toàn giống hệt
        Page<Transaction> transactionsPage = transactionRepository.findByWalletIdOrderByCreatedAtDesc(wallet.getId(), pageable);

        return transactionsPage.map(this::mapToTransactionResponse);
    }
    @Override
    @Transactional
    public DepositResponseDTO createDepositRequest(Long accountId, DepositRequestDTO dto) {
        Wallet wallet = walletRepository.findByAccountId(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found"));

        // 1. Tạo một giao dịch nạp tiền với trạng thái PENDING
        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .amount(dto.getAmount())
                .type(TransactionType.DEPOSIT)
                .status(TransactionStatus.PENDING)
                .build();
        Transaction savedTransaction = transactionRepository.save(transaction);

        // 2. Gọi API của cổng thanh toán để lấy URL
        // ---- GIẢ LẬP LOGIC GỌI CỔNG THANH TOÁN ----
        // Trong thực tế, bạn sẽ dùng SDK của VNPAY/MoMo ở đây
        // URL này sẽ chứa mã giao dịch của bạn để sau này đối soát
        String paymentGatewayUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?orderId=" + savedTransaction.getId();
        // ------------------------------------------

        return DepositResponseDTO.builder()
                .transactionId(savedTransaction.getId())
                .paymentUrl(paymentGatewayUrl)
                .build();
    }
    @Override
    @Transactional
    public void processPaymentCallback(String transactionId, String status) {
        Long txId = Long.parseLong(transactionId);
        Transaction transaction = transactionRepository.findById(txId)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));

        // Chỉ xử lý giao dịch đang chờ
        if (transaction.getStatus() != TransactionStatus.PENDING) {
            return;
        }

        if ("SUCCESS".equalsIgnoreCase(status)) {
            // 1. Cập nhật trạng thái giao dịch
            transaction.setStatus(TransactionStatus.SUCCESS);

            // 2. Cộng tiền vào ví người dùng
            Wallet wallet = transaction.getWallet();
            wallet.setBalance(wallet.getBalance().add(transaction.getAmount()));
            walletRepository.save(wallet);

        } else {
            // Nếu giao dịch thất bại
            transaction.setStatus(TransactionStatus.FAILED);
        }
        transactionRepository.save(transaction);
    }

    @Override
    @Transactional(readOnly = true) // Giao dịch chỉ đọc, hiệu năng tốt hơn
    public Page<TransactionResponseDTO> getMyTransactionHistory(Long accountId, Pageable pageable) {
        // 1. Tìm ví của người dùng
        Wallet wallet = walletRepository.findByAccountId(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found for this account"));

        // 2. Lấy danh sách giao dịch đã phân trang từ repository
        Page<Transaction> transactionsPage = transactionRepository.findByWalletIdOrderByCreatedAtDesc(wallet.getId(), pageable);

        // 3. Chuyển đổi (map) Page<Transaction> sang Page<TransactionResponseDTO>
        return transactionsPage.map(this::mapToTransactionResponse);
    }
    private TransactionResponseDTO mapToTransactionResponse(Transaction transaction) {
        return TransactionResponseDTO.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount())
                .type(transaction.getType())
                .status(transaction.getStatus())
                .paymentId(transaction.getPaymentId())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
    private PaymentResponseDTO mapToResponse(ProjectPayment payment) {
        Account recipient = payment.getProject().getStartup();
        Account payer = payment.getPayer();

        String payerName = (payer.getInvestor() != null && payer.getInvestor().getFullName() != null)
                ? payer.getInvestor().getFullName() : payer.getEmail();

        String recipientName = (recipient.getStartup() != null && recipient.getStartup().getStartupName() != null)
                ? recipient.getStartup().getStartupName() : recipient.getEmail();

        return PaymentResponseDTO.builder()
                .id(payment.getId())
                .projectId(payment.getProject().getId())
                .projectName(payment.getProject().getProjectName())
                .payerId(payer.getId())
                .payerName(payerName)
                .recipientId(recipient.getId())
                .recipientName(recipientName)
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}