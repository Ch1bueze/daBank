package com.fintech.dabankapp.service.impl;

import com.fintech.dabankapp.dto.TransactionDto;
import com.fintech.dabankapp.entity.Transaction;
import com.fintech.dabankapp.repository.TransactionRepository;
import com.fintech.dabankapp.service.TransactionService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;



@RequiredArgsConstructor
@Service
@Component
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    @Override
    public void saveTransaction(TransactionDto transactionDto) {
        Transaction newTransaction = Transaction.builder()
                .transactionType(transactionDto.getTransactionType())
                .accountNumber(transactionDto.getAccountNumber())
                .amount(transactionDto.getAmount())
                .status("SUCCESS")
                .build();
        transactionRepository.save(newTransaction);
        System.out.println("Transaction saved successfully");
    }
}
