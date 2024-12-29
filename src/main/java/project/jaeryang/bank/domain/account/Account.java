package project.jaeryang.bank.domain.account;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.jaeryang.bank.domain.common.BaseTimeEntity;
import project.jaeryang.bank.domain.user.User;

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
}
