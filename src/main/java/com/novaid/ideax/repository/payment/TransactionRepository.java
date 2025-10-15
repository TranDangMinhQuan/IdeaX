package com.novaid.ideax.repository.payment;
import com.novaid.ideax.entity.payment.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Tìm tất cả giao dịch của một ví, sắp xếp theo thời gian mới nhất trước.
     * Pageable sẽ xử lý việc phân trang.
     */
    Page<Transaction> findByWalletIdOrderByCreatedAtDesc(Long walletId, Pageable pageable);
}