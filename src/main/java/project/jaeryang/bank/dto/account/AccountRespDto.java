package project.jaeryang.bank.dto.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import project.jaeryang.bank.domain.account.Account;
import project.jaeryang.bank.domain.transaction.Transaction;
import project.jaeryang.bank.domain.user.User;
import project.jaeryang.bank.util.CustomDateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AccountRespDto {
    @Getter
    @Setter
    public static class AccountSaveRespDto {
        private Long id;
        private Long number;
        private Long balance;

        public AccountSaveRespDto(Account account) {
            this.id = account.getId();
            this.number = account.getNumber();
            this.balance = account.getBalance();
        }
    }

    @Getter
    @Setter
    public static class AccountListRespDto {
        private String fullname;
        private List<AccountDto> accounts = new ArrayList<>();

        public AccountListRespDto(User user, List<Account> accounts) {
            this.fullname = user.getFullname();
            this.accounts = accounts.stream().map(AccountDto::new).collect(Collectors.toList());
        }

        @Getter
        @Setter
        public class AccountDto {
            private Long id;
            private Long number;
            private Long balance;

            public AccountDto(Account account) {
                this.id = account.getId();
                this.number = account.getNumber();
                this.balance = account.getBalance();
            }
        }
    }

    @Getter
    @Setter
    public static class AccountDepositRespDto {
        private Long id;
        private Long number;
        private TransactionDto transactionDto;

        public AccountDepositRespDto(Account account, Transaction transaction) {
            this.id = account.getId();
            this.number = account.getNumber();
            this.transactionDto = new TransactionDto(transaction);
        }

        @Getter
        @Setter
        public static class TransactionDto {
            private Long id;
            private String transactionType;
            private String sender;
            private String receiver;
            private Long amount;
            private String tel;
            private String createdAt;
            @JsonIgnore
            private Long depositAccountBalance; //클라이언트에게 전달x, 서비스단에서 테스트 용도

            public TransactionDto(Transaction transaction) {
                this.id = transaction.getId();
                this.transactionType = transaction.getTransactionType().getValue();
                this.sender = transaction.getSender();
                this.receiver = transaction.getReceiver();
                this.amount = transaction.getAmount();
                this.tel = transaction.getTel();
                this.createdAt = CustomDateUtil.toStringFormat(transaction.getCreatedAt());
                this.depositAccountBalance = transaction.getDepositAccountBalance();
            }
        }
    }

    /**
     * DTO가 똑같아도 재사용하지 말기!
     * 나중에 응답 내용이 달라질 때 DTO를 수정하면 여러 곳에 영향을 주게 된다.
     * => DTO는 독립적으로 만들자
     */
    @Getter
    @Setter
    public static class AccountWithdrawRespDto {
        private Long id;
        private Long number;
        private Long balance;
        private TransactionDto transactionDto;

        public AccountWithdrawRespDto(Account account, Transaction transaction) {
            this.id = account.getId();
            this.number = account.getNumber();
            this.balance = account.getBalance();
            this.transactionDto = new TransactionDto(transaction);
        }

        @Getter
        @Setter
        public static class TransactionDto {
            private Long id;
            private String transactionType;
            private String sender;
            private String receiver;
            private Long amount;
            private String createdAt;

            public TransactionDto(Transaction transaction) {
                this.id = transaction.getId();
                this.transactionType = transaction.getTransactionType().getValue();
                this.sender = transaction.getSender();
                this.receiver = transaction.getReceiver();
                this.amount = transaction.getAmount();
                this.createdAt = CustomDateUtil.toStringFormat(transaction.getCreatedAt());
            }
        }
    }

    @Getter
    @Setter
    public static class AccountTransferRespDto {
        private Long id;
        private Long number;
        private Long balance;
        private TransactionDto transactionDto;

        public AccountTransferRespDto(Account account, Transaction transaction) {
            this.id = account.getId();
            this.number = account.getNumber();
            this.balance = account.getBalance();
            this.transactionDto = new TransactionDto(transaction);
        }

        @Getter
        @Setter
        public static class TransactionDto {
            private Long id;
            private String transactionType;
            private String sender;
            private String receiver;
            private Long amount;
            @JsonIgnore
            private Long depositAccountBalance;
            private String createdAt;

            public TransactionDto(Transaction transaction) {
                this.id = transaction.getId();
                this.transactionType = transaction.getTransactionType().getValue();
                this.sender = transaction.getSender();
                this.receiver = transaction.getReceiver();
                this.amount = transaction.getAmount();
                this.depositAccountBalance = transaction.getDepositAccountBalance();
                this.createdAt = CustomDateUtil.toStringFormat(transaction.getCreatedAt());
            }
        }
    }
}
