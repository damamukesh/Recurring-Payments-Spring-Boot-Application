package com.demo.recpay.recpay.repository;

import com.demo.recpay.recpay.model.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionsRepository extends JpaRepository<Transactions, Integer> {
}
