package com.demo.recpay.recpay.repository;

import com.demo.recpay.recpay.model.RecurringPayments;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecurringPaymentsRepository extends JpaRepository<RecurringPayments, Integer> {
}
