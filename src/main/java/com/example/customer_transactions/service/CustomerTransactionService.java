package com.example.customer_transactions.service;

import com.example.customer_transactions.entity.CustomerAccount;
import com.example.customer_transactions.entity.CustomerTransaction;
import com.example.customer_transactions.repository.CustomerAccountRepository;
import com.example.customer_transactions.repository.CustomerTransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Service
public class CustomerTransactionService {

    private final CustomerAccountRepository accountRepository;
    private final CustomerTransactionRepository transactionRepository;

    public CustomerTransactionService(
            CustomerAccountRepository accountRepository,
            CustomerTransactionRepository transactionRepository
    ) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public CustomerTransaction transfer(CustomerTransaction transaction) {
        if (transaction == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Request");
        }

        if (transaction.getFromAccountId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Request");
        }

        if (transaction.getToAccountId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Request");
        }

        BigDecimal transferAmount = transaction.getAmount();

        if (transferAmount == null || transferAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid amount");
        }

        CustomerAccount fromAccount = accountRepository.findById(transaction.getFromAccountId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Source account not found"));

        CustomerAccount toAccount = accountRepository.findById(transaction.getToAccountId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Destination account not found"));

        if (fromAccount.getAmount().compareTo(transferAmount) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient funds");
        }

        fromAccount.setAmount(fromAccount.getAmount().subtract(transferAmount));
        toAccount.setAmount(toAccount.getAmount().add(transferAmount));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        return transactionRepository.save(transaction);
    }
}