package project.jaeryang.bank.domain.account;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    // TODO : User 객체 즉시 조회로 변경
    Optional<Account> findByAccountNumber(Long accountNumber);
}
