package com.fintech.dabankapp.service.impl;

import com.fintech.dabankapp.dto.*;
import com.fintech.dabankapp.entity.AppUser;
import com.fintech.dabankapp.repository.UserRepository;
import com.fintech.dabankapp.service.EmailService;
import com.fintech.dabankapp.service.TransactionService;
import com.fintech.dabankapp.service.UserService;
import com.fintech.dabankapp.util.AccountUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final TransactionService transactionService;
    @Override
    public BankResponse createAccount(UserRequest userRequest) {
        if (userRepository.existsByEmail(userRequest.getEmail())){
             return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
                     .accountInfo(null)
                    .build();
        }

        AppUser newUser = AppUser.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .otherName(userRequest.getOtherName())
                .email(userRequest.getEmail())
                .phoneNumber(userRequest.getPhoneNumber())
                .altPhoneNumber(userRequest.getAltPhoneNumber())
                .address(userRequest.getAddress())
                .gender(userRequest.getGender())
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .accountNumber(AccountUtils.generateAccountNumber())
                .accountBalance(BigDecimal.ZERO)
                .status("ACTIVE")
                .build();

        AppUser savedUser = userRepository.save(newUser);

        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(savedUser.getEmail())
                .subject("ACCOUNT CREATION SUCCESS")
                .messageBody("Congratulations " + savedUser.getFirstName() + "! Your DaBank account was created successfully\n\nAccount Details:\n"
                        + "Account Name: "+ savedUser.getLastName() + " " + savedUser.getFirstName() + " " + savedUser.getOtherName()
                        + "\nAccount Number: " + savedUser.getAccountNumber())
                .build();
        emailService.sendMail(emailDetails);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATED_CODE)
                .responseMessage(AccountUtils.ACCOUNT_CREATED_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(savedUser.getAccountBalance())
                        .accountNumber(savedUser.getAccountNumber())
                        .accountName(savedUser.getLastName() + " " + savedUser.getFirstName() + " " + savedUser.getOtherName())
                        .build())
                .build();
    }

    @Override
    public BankResponse balanceEnquiry(EnquiryRequest enquiryRequest) {
        Boolean isAccountExist = userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber());
        if (!isAccountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        AppUser foundUser = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber());
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                .responseMessage(AccountUtils.ACCOUNT_FOUND_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(foundUser.getLastName() + " " + foundUser.getFirstName() + " " + foundUser.getOtherName())
                        .accountNumber(foundUser.getAccountNumber())
                        .accountBalance(foundUser.getAccountBalance())
                        .build())
                .build();
    }

    @Override
    public String nameEnquiry(EnquiryRequest enquiryRequest) {
        Boolean isAccountExist = userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber());
        if (!isAccountExist) {
            return AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE;
        }
        AppUser foundUser = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber());
        return foundUser.getLastName() + " " + foundUser.getFirstName() + " " + foundUser.getOtherName();
    }

    @Override
    public BankResponse creditAccount(CreditDebitRequest request) {
        Boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        AppUser userToCredit = userRepository.findByAccountNumber(request.getAccountNumber());
        userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(request.getAmount()));
        AppUser creditedUser = userRepository.save(userToCredit);

        TransactionDto transactionDto = TransactionDto.builder()
                .transactionType("CREDIT")
                .amount(request.getAmount())
                .accountNumber(request.getAccountNumber())
                .build();
        transactionService.saveTransaction(transactionDto);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREDITED_CODE)
                .responseMessage(AccountUtils.ACCOUNT_CREDITED_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(creditedUser.getLastName() + " " + creditedUser.getFirstName() + " " + creditedUser.getOtherName())
                        .accountNumber(creditedUser.getAccountNumber())
                        .accountBalance(creditedUser.getAccountBalance())
                        .build())
                .build();
    }

    @Override
    public BankResponse debitAccount(CreditDebitRequest request) {
        Boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        AppUser userToDebit = userRepository.findByAccountNumber(request.getAccountNumber());
        if (userToDebit.getAccountBalance().compareTo(request.getAmount()) < 0){
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                            .accountName(userToDebit.getLastName() + " " + userToDebit.getFirstName() + " " + userToDebit.getOtherName())
                            .accountNumber(userToDebit.getAccountNumber())
                            .accountBalance(userToDebit.getAccountBalance())
                            .build())
                    .build();
        }

        userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(request.getAmount()));
        AppUser debitedUser = userRepository.save(userToDebit);

        TransactionDto transactionDto = TransactionDto.builder()
                .transactionType("DEBIT")
                .amount(request.getAmount())
                .accountNumber(request.getAccountNumber())
                .build();
        transactionService.saveTransaction(transactionDto);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_DEBITED_CODE)
                .responseMessage(AccountUtils.ACCOUNT_DEBITED_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(debitedUser.getLastName() + " " + debitedUser.getFirstName() + " " + debitedUser.getOtherName())
                        .accountNumber(debitedUser.getAccountNumber())
                        .accountBalance(debitedUser.getAccountBalance())
                        .build())
                .build();
    }

    @Override
    public BankResponse transfer(TransferRequest transferRequest) {
        Boolean doesAccountExist = userRepository.existsByAccountNumber(transferRequest.getSenderAccountNumber());
        Boolean isRecipientAccountExist = userRepository.existsByAccountNumber(transferRequest.getRecipientAccountNumber());
        if (!doesAccountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        if (!isRecipientAccountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        AppUser sender = userRepository.findByAccountNumber(transferRequest.getSenderAccountNumber());
        if (transferRequest.getAmount().compareTo(sender.getAccountBalance()) > 0){
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                            .accountName(sender.getLastName() + " " + sender.getFirstName() + " " + sender.getOtherName())
                            .accountNumber(sender.getAccountNumber())
                            .accountBalance(sender.getAccountBalance())
                            .build())
                    .build();
        }

        sender.setAccountBalance(sender.getAccountBalance().subtract(transferRequest.getAmount()));
        AppUser debitedUser = userRepository.save(sender);
        EmailDetails debitAlert = EmailDetails.builder()
                .recipient(debitedUser.getEmail())
                .subject("Debit Alert")
                .messageBody("Hello " + debitedUser.getFirstName() + ",\n" + transferRequest.getAmount()
                        + " has been debited from your account, Your remaining balance is: " + debitedUser.getAccountBalance())
                .build();
        emailService.sendMail(debitAlert);

        AppUser recipient = userRepository.findByAccountNumber(transferRequest.getRecipientAccountNumber());
        recipient.setAccountBalance(recipient.getAccountBalance().add(transferRequest.getAmount()));
        userRepository.save(recipient);

        TransactionDto transactionDto = TransactionDto.builder()
                .transactionType("CREDIT")
                .amount(transferRequest.getAmount())
                .accountNumber(transferRequest.getRecipientAccountNumber())
                .build();
        transactionService.saveTransaction(transactionDto);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_DEBITED_CODE)
                .responseMessage(AccountUtils.ACCOUNT_DEBITED_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(debitedUser.getLastName() + " " + debitedUser.getFirstName() + " " + debitedUser.getOtherName())
                        .accountNumber(debitedUser.getAccountNumber())
                        .accountBalance(debitedUser.getAccountBalance())
                        .build())
                .build();
    }
}
