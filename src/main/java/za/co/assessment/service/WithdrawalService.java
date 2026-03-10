package za.co.assessment.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.co.assessment.event.WithdrawalEvent;
import za.co.assessment.event.WithdrawalEventPublisher;
import za.co.assessment.entity.Account;
import za.co.assessment.repository.AccountRepository;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Slf4j
public class WithdrawalService {

    private final AccountRepository accountRepository;
    private final WithdrawalEventPublisher withdrawalEventPublisher;

    public WithdrawalService(AccountRepository accountRepository,
                             WithdrawalEventPublisher withdrawalEventPublisher) {
        this.accountRepository = accountRepository;
        this.withdrawalEventPublisher = withdrawalEventPublisher;
    }

    @Transactional
    public String withdraw(Long accountId, BigDecimal amount) {
            log.info("Received withdraw request for accountId={}, amount={}", accountId, amount);
            Optional<Account> accountOptional = accountRepository.findById(accountId);
            if (accountOptional.isEmpty()) {
                log.info("Account not found for accountId={}", accountId);
                return "Account not found";
            }

            Account account = accountOptional.get();
            if (account.getBalance().compareTo(amount) < 0) {
                log.info("Insufficient funds for withdrawal accountId={}, amount={}", accountId, amount);
                return "Insufficient funds for withdrawal";
            }

            account.setBalance(account.getBalance().subtract(amount));
            accountRepository.save(account);

            try {
                WithdrawalEvent event = new WithdrawalEvent(amount, accountId, "SUCCESSFUL");
                withdrawalEventPublisher.publish(event);
            } catch (Exception e) {
                log.error("Failed to publish Withdrawal Event for accountId={}", accountId, e);
            }

            log.info("Completed withdraw request for accountId={}, amount={}", accountId, amount);

            return "Withdrawal successful";
    }
}