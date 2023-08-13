package com.fintech.dabankapp.util;

import java.time.Year;

public class AccountUtils {

    public static final String ACCOUNT_EXISTS_CODE = "001";
    public static final String ACCOUNT_EXISTS_MESSAGE = "Account with this email already exists";
    public static final String ACCOUNT_CREATED_CODE = "002";
    public static final String ACCOUNT_CREATED_MESSAGE = "Account created Successfully";
    public static final String ACCOUNT_NOT_EXIST_CODE = "003";
    public static final String ACCOUNT_NOT_EXIST_MESSAGE = "User with provided account number does not exist";
    public static final String ACCOUNT_FOUND_CODE = "004";
    public static final String ACCOUNT_FOUND_MESSAGE = "User with account found";
    public static final String ACCOUNT_CREDITED_CODE = "005";
    public static final String ACCOUNT_CREDITED_MESSAGE = "Transaction successful. Account credited successfully";
    public static final String INSUFFICIENT_BALANCE_CODE = "007";
    public static final String INSUFFICIENT_BALANCE_MESSAGE = "Transaction failed. Insufficient balance";
    public static final String ACCOUNT_DEBITED_CODE = "008";
    public static final String ACCOUNT_DEBITED_MESSAGE = "Transaction successful. Account debited successfully";

    public static String generateAccountNumber(){
        Year currentYear = Year.now();
        int min = 100000;
        int max = 999999;
        int randomNumber = (int) Math.floor(Math.random() * (max - min + 1) + min);
        String year = String.valueOf(currentYear);
        String number = String.valueOf(randomNumber);
        return year + number;
    }

}
