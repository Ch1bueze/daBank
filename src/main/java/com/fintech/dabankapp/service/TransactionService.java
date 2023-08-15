package com.fintech.dabankapp.service;

import com.fintech.dabankapp.dto.TransactionDto;
import com.fintech.dabankapp.entity.Transaction;

public interface TransactionService {
    void saveTransaction(TransactionDto transaction);
}
