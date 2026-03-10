package za.co.assessment.controller;

import org.springframework.web.bind.annotation.*;
import za.co.assessment.service.WithdrawalService;

import java.math.BigDecimal;

@RestController
@RequestMapping("/bank")
public class BankAccountController {
    private final WithdrawalService withdrawalService;

    public BankAccountController(WithdrawalService withdrawalService) {
        this.withdrawalService = withdrawalService;
    }

    @PostMapping("/withdraw")
    public String withdraw(@RequestParam("accountId") Long accountId,
                           @RequestParam("amount") BigDecimal amount) {

        return withdrawalService.withdraw(accountId, amount);

    }
}