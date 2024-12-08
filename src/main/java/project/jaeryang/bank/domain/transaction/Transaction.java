package project.jaeryang.bank.domain.transaction;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.jaeryang.bank.domain.account.Account;
import project.jaeryang.bank.domain.common.BaseTimeEntity;

@NoArgsConstructor
@Getter
@Table(name = "transaction_tb")
@Entity
public class Transaction extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "withdrawAccount_id")
    private Account withdrawAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "depositAccount_id")
    private Account depositAccount;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionEnum transactionType; //WITHDRAW, DEPOSIT, TRANSFER, ALL

    //거래내역 히스토리를 남기기 위해
    private Long withdrawAccountBalance;
    private Long depositAccountBalance;

    //계좌가 사라져도 로그는 남아야한다.
    private String sender;
    private String receiver;
    private String tel;


    @Builder
    public Transaction(Long id, Account withdrawAccount, Account depositAccount, Long amount, TransactionEnum transactionType, Long withdrawAccountBalance, Long depositAccountBalance, String sender, String receiver, String tel) {
        this.id = id;
        this.withdrawAccount = withdrawAccount;
        this.depositAccount = depositAccount;
        this.amount = amount;
        this.transactionType = transactionType;
        this.withdrawAccountBalance = withdrawAccountBalance;
        this.depositAccountBalance = depositAccountBalance;
        this.sender = sender;
        this.receiver = receiver;
        this.tel = tel;
    }
}
