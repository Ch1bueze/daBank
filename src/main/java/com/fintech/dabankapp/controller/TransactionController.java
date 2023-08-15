package com.fintech.dabankapp.controller;

import com.fintech.dabankapp.entity.Transaction;
import com.fintech.dabankapp.service.impl.BankStatement;
import com.itextpdf.text.DocumentException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/bankStatement")
@RequiredArgsConstructor
public class TransactionController {
    private final BankStatement bankStatement;
    @GetMapping
    public ResponseEntity<List<Transaction>> generateBankStatement(@RequestParam String accountNumber,
                                                                   @RequestParam String startDate,
                                                                   @RequestParam String endDate) throws DocumentException, FileNotFoundException {
        return new ResponseEntity<>(bankStatement.generateStatement(accountNumber, startDate, endDate), HttpStatus.CREATED);
    }
}
