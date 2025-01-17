package project.jaeryang.bank.domain.account;


import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.jaeryang.bank.domain.common.BaseTimeEntity;
import project.jaeryang.bank.domain.user.User;
import project.jaeryang.bank.ex.CustomApiException;

@NoArgsConstructor
@Getter
@Table(name = "account_tb")
@Entity
public class Account extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 4)
    private Long number; //계좌번호

    @Column(nullable = false, length = 4)
    private Long password; //계좌 비밀번호

    @Column(nullable = false)
    private Long balance; //잔액 (기본값 1000원)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") //Join FK
    private User user; // 기본 user_id

    @Builder
    public Account(Long id, Long number, Long password, Long balance, User user) {
        this.id = id;
        this.number = number;
        this.password = password;
        this.balance = balance;
        this.user = user;
    }

    public void checkOwner(Long userId) {
        if (!userId.equals(this.user.getId())) //Account 테이블에 userId가 FK로 이미 존재하므로 Lazy loading X
            throw new CustomApiException("계좌 소유자가 아닙니다.");
    }

    public void checkPassword(Long password) {
        if (!password.equals(this.password))
            throw new CustomApiException("계좌 비밀번호가 틀렸습니다.");
    }

    public void deposit(Long amount) {
        balance += amount;
    }

    public void withdraw(Long amount) {
        checkBalance(amount);
        balance -= amount;
    }

    public void checkBalance(Long amount) {
        if (balance < amount)
            throw new CustomApiException("잔액이 부족합니다.");
    }
}
