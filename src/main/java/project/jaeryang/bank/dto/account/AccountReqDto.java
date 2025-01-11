package project.jaeryang.bank.dto.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import project.jaeryang.bank.domain.account.Account;
import project.jaeryang.bank.domain.transaction.Transaction;
import project.jaeryang.bank.domain.user.User;
import project.jaeryang.bank.util.CustomDateUtil;

public class AccountReqDto {
    @Getter
    @Setter
    public static class AccountSaveReqDto {
        @NotNull
        @Digits(integer = 4, fraction = 4)
        private Long number;
        @NotNull
        @Digits(integer = 4, fraction = 4)
        private Long password;

        public Account toEntity(User user) {
            return Account.builder()
                    .number(this.number)
                    .password(this.password)
                    .balance(1000L)
                    .user(user)
                    .build();
        }
    }

    @Getter
    @Setter
    public static class AccountDepositReqDto {
        @NotNull
        @Digits(integer = 4, fraction = 4)
        private Long number;
        @NotNull
        private Long amount;
        @NotEmpty
        @Pattern(regexp = "^(DEPOSIT)$")
        private String transactionType;
        @NotEmpty
        @Pattern(regexp = "^[0-9]{3}[0-9]{4}[0-9]{4}")
        private String tel;
    }


    @Getter
    @Setter
    public static class AccountWithdrawReqDto {
        @NotNull
        @Digits(integer = 4, fraction = 4)
        private Long number;
        @NotNull
        @Digits(integer = 4, fraction = 4)
        private Long password;
        @NotNull
        private Long amount;
        @NotEmpty
        @Pattern(regexp = "^(WTIHDRAW)$")
        private String transactionType;
    }
}
