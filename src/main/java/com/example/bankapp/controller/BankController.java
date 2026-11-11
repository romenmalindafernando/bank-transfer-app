package com.example.bankapp.controller;

import com.example.bankapp.dto.TransferRequest;
import com.example.bankapp.model.Customer;
import com.example.bankapp.repository.CustomerRepository;
import com.example.bankapp.repository.TransactionRepository;
import com.example.bankapp.service.BankTransferService;
import com.example.bankapp.service.FailedTransactionService;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;

@Controller
public class BankController {

    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;
    private final BankTransferService bankTransferService;
    private final FailedTransactionService failedTransactionService;

    public BankController(
            CustomerRepository customerRepository,
            TransactionRepository transactionRepository,
            BankTransferService bankTransferService,
            FailedTransactionService failedTransactionService) {

        this.customerRepository = customerRepository;
        this.transactionRepository = transactionRepository;
        this.bankTransferService = bankTransferService;
        this.failedTransactionService = failedTransactionService;
    }

    @GetMapping("/")
    public String dashboard(Model model) {

        model.addAttribute("customers", customerRepository.findAll());

        model.addAttribute(
                "transactions",
                transactionRepository.findAll().stream()
                        .sorted(Comparator.comparing(
                                t -> t.getCreatedAt(),
                                Comparator.nullsLast(Comparator.reverseOrder())
                        ))
                        .toList()
        );

        model.addAttribute("transferRequest", new TransferRequest());

        return "dashboard";
    }

    @PostMapping("/transfer")
    public String transfer(
            @Valid @ModelAttribute("transferRequest") TransferRequest request,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                    "error", "Please enter valid transfer details."
            );
            return "redirect:/";
        }

        try {
            bankTransferService.transfer(request);

            redirectAttributes.addFlashAttribute(
                    "success", "Transfer completed successfully!"
            );

        } catch (Exception e) {

            failedTransactionService.recordFailure(
                    request.getFromAccount(),
                    request.getToAccount(),
                    request.getAmount(),
                    e.getMessage()
            );

            redirectAttributes.addFlashAttribute(
                    "error", e.getMessage()
            );
        }

        return "redirect:/";
    }

    @GetMapping("/history")
public String accountHistory(
        @RequestParam("account") String account,
        Model model) {

    Customer customer = customerRepository
            .findByAccountNumber(account)
            .orElseThrow(() ->
                    new IllegalArgumentException("Account not found."));

    model.addAttribute("customer", customer);

    model.addAttribute(
            "transactions",
            transactionRepository.findAccountHistory(account)
    );

    return "history";
}
}