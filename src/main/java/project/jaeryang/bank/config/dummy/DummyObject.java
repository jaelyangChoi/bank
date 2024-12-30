package project.jaeryang.bank.config.dummy;

import lombok.Getter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import project.jaeryang.bank.domain.account.Account;
import project.jaeryang.bank.domain.user.User;
import project.jaeryang.bank.domain.user.UserEnum;

@Getter
public class DummyObject {

    protected User newUser(String username, String fullname) {
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        String encPassword = passwordEncoder.encode("1234");

        return User.builder()
                .username(username)
                .password(encPassword)
                .email(username + "@naver.com")
                .fullname(fullname)
                .role(UserEnum.CUSTOMER)
                .build();
    }

    protected User newMockUser(Long id, String username, String fullname) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encPassword = passwordEncoder.encode("1234");

        return User.builder()
                .id(id)
                .username(username)
                .password(encPassword)
                .email(username + "@naver.com")
                .fullname(fullname)
                .role(UserEnum.CUSTOMER)
                .build();
    }

    protected Account newAccount(Long number, User user) {
        return Account.builder()
                .number(number)
                .password(1234L)
                .balance(1000L)
                .user(user)
                .build();
    }

    protected Account newMockAccount(Long id, Long number, Long balance, User user) {
        return Account.builder()
                .id(id)
                .number(number)
                .password(1234L)
                .balance(balance)
                .user(user)
                .build();
    }
}
