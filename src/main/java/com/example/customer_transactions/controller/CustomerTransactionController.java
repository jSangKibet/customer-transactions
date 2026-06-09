package com.example.customer_transactions.controller;

import com.example.customer_transactions.entity.CustomerAccount;
import com.example.customer_transactions.repository.CustomerAccountRepository;
import com.example.customer_transactions.entity.CustomerTransaction;
import com.example.customer_transactions.service.CustomerTransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1")
public class CustomerTransactionController {

    private final CustomerAccountRepository accountRepository;
    private final CustomerTransactionService transactionService;

    public CustomerTransactionController(
            CustomerAccountRepository accountRepository,
            CustomerTransactionService transactionService
    ) {
        this.accountRepository = accountRepository;
        this.transactionService = transactionService;
    }

    @PostMapping("/accounts")
    public ResponseEntity<CustomerAccount> createAccount(@RequestBody CustomerAccount account) {
        CustomerAccount savedAccount = accountRepository.save(account);
        return new ResponseEntity<>(savedAccount, HttpStatus.CREATED);
    }

    @GetMapping("/accounts/{id}")
    public ResponseEntity<CustomerAccount> getAccount(@PathVariable Long id) {
        return accountRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
    }

    @PostMapping("/transfers")
    public ResponseEntity<CustomerTransaction> transfer(@RequestBody CustomerTransaction transaction) {
        CustomerTransaction savedTransaction = transactionService.transfer(transaction);
        return new ResponseEntity<>(savedTransaction, HttpStatus.CREATED);
    }
}