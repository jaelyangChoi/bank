package project.jaeryang.bank.dto.account;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import project.jaeryang.bank.domain.account.Account;
import project.jaeryang.bank.domain.user.User;

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
}
