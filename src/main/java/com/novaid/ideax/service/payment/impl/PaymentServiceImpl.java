package com.novaid.ideax.service.payment.impl;

import com.novaid.ideax.dto.payment.*;
import com.novaid.ideax.entity.auth.Account;
import com.novaid.ideax.entity.payment.ProjectPayment;
import com.novaid.ideax.entity.payment.Transaction;
import com.novaid.ideax.entity.payment.Wallet;
import com.novaid.ideax.entity.project.Project;
import com.novaid.ideax.config.VnpayConfig; // Import config
import jakarta.servlet.http.HttpServletRequest; // Import để lấy IP
import com.novaid.ideax.enums.PaymentStatus;
import com.novaid.ideax.enums.TransactionStatus;
import com.novaid.ideax.enums.TransactionType;
import com.novaid.ideax.exception.InsufficientFundsException;
import com.novaid.ideax.repository.auth.AccountRepository;
import com.novaid.ideax.repository.payment.ProjectPaymentRepository;
import com.novaid.ideax.repository.payment.TransactionRepository;
import com.novaid.ideax.repository.payment.WalletRepository;
import com.novaid.ideax.repository.project.ProjectRepository;
import com.novaid.ideax.service.payment.PaymentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final AccountRepository accountRepository;
    private final ProjectRepository projectRepository;
    private final WalletRepository walletRepository;
    private final ProjectPaymentRepository paymentRepository;
    private final TransactionRepository transactionRepository;
    private final VnpayConfig vnpayConfig;

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
    public DepositResponseDTO createDepositRequest(Long accountId, DepositRequestDTO dto, HttpServletRequest request) {
        Wallet wallet = walletRepository.findByAccountId(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found"));

        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .amount(dto.getAmount())
                .type(TransactionType.DEPOSIT)
                .status(TransactionStatus.PENDING)
                .build();
        Transaction savedTransaction = transactionRepository.save(transaction);

        // Tạo URL thanh toán VNPAY
        String paymentUrl = createVnpayPaymentUrl(savedTransaction, request);

        return DepositResponseDTO.builder()
                .transactionId(savedTransaction.getId())
                .paymentUrl(paymentUrl)
                .build();
    }

    private String createVnpayPaymentUrl(Transaction transaction, HttpServletRequest request) {
        long amount = transaction.getAmount().multiply(new BigDecimal(100)).longValue(); // VNPAY yêu cầu nhân 100
        String vnp_TxnRef = String.valueOf(transaction.getId()); // Mã giao dịch của bạn

        // Lấy địa chỉ IP của client
        String vnp_IpAddr = request.getRemoteAddr();

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", vnpayConfig.getTmnCode());
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Nap tien vao vi IdeaX - GD:" + vnp_TxnRef);
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnpayConfig.getReturnUrl());
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        try {
            for (String fieldName : fieldNames) {
                String fieldValue = vnp_Params.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    // Build hash data
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    // Build query
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    if (fieldNames.indexOf(fieldName) < fieldNames.size() - 1) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }
        } catch (Exception e) {
            // Handle exception
        }

        String queryUrl = query.toString();
        String vnp_SecureHash = hmacSHA512(vnpayConfig.getHashSecret(), hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;

        return vnpayConfig.getUrl() + "?" + queryUrl;
    }

    // Hàm băm, bạn có thể copy từ code mẫu của VNPAY
    public static String hmacSHA512(final String key, final String data) {
        try {
            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA512");
            mac.init(new javax.crypto.spec.SecretKeySpec(key.getBytes(), "HmacSHA512"));
            byte[] hash = mac.doFinal(data.getBytes());
            StringBuilder sb = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate HMAC-SHA512", e);
        }
    }
    @Override
    @Transactional
    public void createWithdrawRequest(Long accountId, WithdrawRequestDTO dto) {
        Wallet wallet = walletRepository.findByAccountId(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found"));

        // 1. Kiểm tra số dư
        if (wallet.getBalance().compareTo(dto.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds for withdrawal.");
        }

        // 2. TRỪ TIỀN NGAY LẬP TỨC khỏi ví để tránh người dùng tạo 2 lệnh rút liên tiếp
        wallet.setBalance(wallet.getBalance().subtract(dto.getAmount()));
        walletRepository.save(wallet);

        // 3. Tạo giao dịch rút tiền với trạng thái PENDING (chờ admin duyệt)
        // Thông tin ngân hàng có thể lưu vào một trường description hoặc một bảng riêng
        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .amount(dto.getAmount().negate()) // Giao dịch rút tiền là số âm
                .type(TransactionType.WITHDRAW)
                .status(TransactionStatus.PENDING)
                .build();
        transactionRepository.save(transaction);

        // [Gợi ý] Gửi email/notification cho admin về yêu cầu rút tiền mới
    }
    @Override
    @Transactional
    public String processVnpayIPN(Map<String, String> vnpayParams) {
        // 1. Xác thực chữ ký
        String vnp_SecureHash = vnpayParams.get("vnp_SecureHash");
        vnpayParams.remove("vnp_SecureHash"); // Xóa hash khỏi map trước khi tạo lại hash để so sánh

        String calculatedHash = hmacSHA512(vnpayConfig.getHashSecret(), buildHashData(vnpayParams));

        if (!calculatedHash.equals(vnp_SecureHash)) {
            // Chữ ký không hợp lệ, có thể là giao dịch giả mạo
            return "{\"RspCode\":\"97\",\"Message\":\"Invalid Signature\"}";
        }

        long transactionId = Long.parseLong(vnpayParams.get("vnp_TxnRef"));
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElse(null);

        // 2. Kiểm tra giao dịch có tồn tại và đang chờ xử lý không
        if (transaction == null) {
            return "{\"RspCode\":\"01\",\"Message\":\"Order not found\"}";
        }
        if (transaction.getStatus() != TransactionStatus.PENDING) {
            return "{\"RspCode\":\"02\",\"Message\":\"Order already confirmed\"}";
        }

        // 3. Kiểm tra mã phản hồi từ VNPAY và số tiền
        String vnp_ResponseCode = vnpayParams.get("vnp_ResponseCode");
        long vnpayAmount = Long.parseLong(vnpayParams.get("vnp_Amount")) / 100;

        if (!"00".equals(vnp_ResponseCode)) {
            // Giao dịch thất bại trên VNPAY
            transaction.setStatus(TransactionStatus.FAILED);
        } else if (transaction.getAmount().longValue() != vnpayAmount) {
            // Số tiền không khớp, đánh dấu là FAILED để kiểm tra thủ công
            transaction.setStatus(TransactionStatus.FAILED);
            // TODO: Ghi log hoặc gửi cảnh báo về trường hợp này
        } else {
            // Giao dịch thành công và hợp lệ
            transaction.setStatus(TransactionStatus.SUCCESS);
            Wallet wallet = transaction.getWallet();
            wallet.setBalance(wallet.getBalance().add(transaction.getAmount()));
            walletRepository.save(wallet);
        }

        transactionRepository.save(transaction);
        return "{\"RspCode\":\"00\",\"Message\":\"Confirm Success\"}";
    }

    // Helper để tạo chuỗi hash data, tái sử dụng code
    private String buildHashData(Map<String, String> vnp_Params) {
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        try {
            Iterator<String> itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = itr.next();
                String fieldValue = vnp_Params.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    if (itr.hasNext()) {
                        hashData.append('&');
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return hashData.toString();
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