package project.jaeryang.bank.domain.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

//    @Query("SELECT a from Account a JOIN FETCH a.user u WHERE a.number = :accountNumber")
    Optional<Account> findByNumber(Long accountNumber);

    List<Account> findByUserId(Long userId);
}
