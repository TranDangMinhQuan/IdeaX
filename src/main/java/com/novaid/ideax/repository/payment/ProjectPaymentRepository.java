package com.novaid.ideax.repository.payment;

import com.novaid.ideax.entity.payment.ProjectPayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectPaymentRepository extends JpaRepository<ProjectPayment, Long> {}