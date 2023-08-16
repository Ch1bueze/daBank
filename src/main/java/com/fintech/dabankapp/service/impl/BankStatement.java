package com.fintech.dabankapp.service.impl;

import com.fintech.dabankapp.dto.EmailDetails;
import com.fintech.dabankapp.entity.AppUser;
import com.fintech.dabankapp.entity.Transaction;
import com.fintech.dabankapp.repository.TransactionRepository;
import com.fintech.dabankapp.repository.UserRepository;
import com.fintech.dabankapp.service.EmailService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class BankStatement {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private static final String FILE = "/Users/dec/Desktop/Statements/MyStatement.pdf";

    public List<Transaction> generateStatement(String accountNumber, String startDate, String endDate) throws FileNotFoundException, DocumentException {
        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);

        List<Transaction> transactionList = transactionRepository.findAll().stream().filter(transaction -> transaction.getAccountNumber().equals(accountNumber))
                .filter(transaction -> transaction.getCreatedAt().equals(start))
                .filter(transaction -> transaction.getCreatedAt().equals(end)).toList();

        AppUser user = userRepository.findByAccountNumber(accountNumber);
        String accountName = user.getLastName() + " " + user.getFirstName() + " " + user.getOtherName();


        Rectangle statementSize = new Rectangle(PageSize.A4);
        Document document = new Document(statementSize);
        log.info("Setting document size...");
        OutputStream outputStream = new FileOutputStream(FILE);
        PdfWriter.getInstance(document, outputStream);
        document.open();

        PdfPTable banInfoTable = new PdfPTable(1);
        PdfPCell bankName = new PdfPCell(new Phrase("DaBank"));
        bankName.setBorder(0);
        bankName.setBackgroundColor(BaseColor.BLUE);
        bankName.setPadding(20f);

        PdfPCell bankAddress = new PdfPCell(new Phrase("No 2 Random street address, Lagos, Nigeria"));
        bankAddress.setBorder(0);
        banInfoTable.addCell(bankName);
        banInfoTable.addCell(bankAddress);

        PdfPTable statementInfoTable = new PdfPTable(2);
        PdfPCell startsDate = new PdfPCell(new Phrase("Start Date: " + startDate));
        startsDate.setBorder(0);
        PdfPCell customerInfo = new PdfPCell(new Phrase("STATEMENT OF ACCOUNT"));
        customerInfo.setBorder(0);
        PdfPCell stopsDate = new PdfPCell(new Phrase("End Date: " + endDate));
        stopsDate.setBorder(0);
        PdfPCell customerName = new PdfPCell(new Phrase("Account Name: " + accountName));
        customerName.setBorder(0);
        PdfPCell space = new PdfPCell();
        space.setBorder(0);
        PdfPCell address = new PdfPCell(new Phrase("Customer address: " + user.getAddress()));
        address.setBorder(0);
        statementInfoTable.addCell(startsDate);
        statementInfoTable.addCell(customerInfo);
        statementInfoTable.addCell(stopsDate);
        statementInfoTable.addCell(customerName);
        statementInfoTable.addCell(space);
        statementInfoTable.addCell(address);

        PdfPTable transactionsTable = new PdfPTable(4);
        PdfPCell date = new PdfPCell(new Phrase("DATE"));
        date.setBackgroundColor(BaseColor.BLUE);
        date.setBorder(0);
        PdfPCell transactionType = new PdfPCell(new Phrase("TRANSACTION TYPE"));
        transactionType.setBackgroundColor(BaseColor.BLUE);
        transactionType.setBorder(0);
        PdfPCell transactionAmount = new PdfPCell(new Phrase("AMOUNT"));
        transactionAmount.setBackgroundColor(BaseColor.BLUE);
        transactionAmount.setBorder(0);
        PdfPCell status = new PdfPCell(new Phrase("STATUS"));
        status.setBackgroundColor(BaseColor.BLUE);
        status.setBorder(0);
        transactionsTable.addCell(date);
        transactionsTable.addCell(transactionType);
        transactionsTable.addCell(transactionAmount);
        transactionsTable.addCell(status);

        transactionList.forEach(transaction -> {
            transactionsTable.addCell(new Phrase(transaction.getCreatedAt().toString()));
            transactionsTable.addCell(new Phrase(transaction.getTransactionType()));
            transactionsTable.addCell(new Phrase(transaction.getAmount().toString()));
            transactionsTable.addCell(new Phrase(transaction.getStatus()));
        });

        document.add(banInfoTable);
        document.add(statementInfoTable);
        document.add(transactionsTable);
        document.close();

        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(user.getEmail())
                .subject("STATEMENT OF ACCOUNT")
                .messageBody("Kindly find your statement of account attached to this mail")
                .attachment(FILE)
                .build();

        emailService.sendEmailWithAttachment(emailDetails);

        return transactionList;
    }

}
