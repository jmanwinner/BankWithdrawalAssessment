package za.co.assessment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import za.co.assessment.entity.Account;

import java.math.BigDecimal;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * Atomic Update: Prevent race conditions even if multiple withdrawals happen concurrently
     *
     * Find account by accountId
     * check if the available balance is >= then the withdrawal amount needed
     * then subtract the withdrawal amount from available balance if it's >= then the amount needed to withdraw
     * and then persist the balance in one sql statement
     *
     * return 1 if successful and 0 if not successful
     * **/
    @Modifying(clearAutomatically = true)
    @Query("""
        UPDATE Account a
        SET a.balance = a.balance - :amount
        WHERE a.id = :accountId
        AND a.balance >= :amount
        """)
    int withdrawFromAccount(Long accountId, BigDecimal amount);

}