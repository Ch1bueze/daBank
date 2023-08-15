package com.fintech.dabankapp.controller;

import com.fintech.dabankapp.dto.*;
import com.fintech.dabankapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/user")
@RequiredArgsConstructor
@Tag(name = "User Management APIs")
public class UserController {
    private final UserService userService;
    @PostMapping("create")
    @Operation(summary = "Create a new user with a unique account number")
    public ResponseEntity<BankResponse> createAccount(@RequestBody UserRequest userRequest){
        return new ResponseEntity<>(userService.createAccount(userRequest), HttpStatus.CREATED);
    }
    @GetMapping("/balance-enquiry")
    public ResponseEntity<BankResponse> balanceEnquiry(@RequestBody EnquiryRequest enquiryRequest){
        return new ResponseEntity<>(userService.balanceEnquiry(enquiryRequest), HttpStatus.OK);
    }
    @GetMapping("/name-enquiry")
    public ResponseEntity<String> nameEnquiry(@RequestBody EnquiryRequest enquiryRequest){
        return new ResponseEntity<>(userService.nameEnquiry(enquiryRequest), HttpStatus.OK);
    }
    @PostMapping("/credit")
    public ResponseEntity<BankResponse> creditAccount(@RequestBody CreditDebitRequest request){
        return new ResponseEntity<>(userService.creditAccount(request),HttpStatus.ACCEPTED);
    }
    @PostMapping("/debit")
    public ResponseEntity<BankResponse> debitAccount(@RequestBody CreditDebitRequest request){
        return new ResponseEntity<>(userService.debitAccount(request), HttpStatus.ACCEPTED);
    }
    @PostMapping("/transfer")
    public ResponseEntity<BankResponse> transfer(@RequestBody TransferRequest request){
        return new ResponseEntity<>(userService.transfer(request), HttpStatus.ACCEPTED);
    }
}
