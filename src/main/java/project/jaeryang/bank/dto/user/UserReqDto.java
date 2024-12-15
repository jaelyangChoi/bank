package project.jaeryang.bank.dto.user;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;
import project.jaeryang.bank.domain.user.User;
import project.jaeryang.bank.domain.user.UserEnum;

public class UserReqDto {
    @Getter
    @Setter
    public static class JoinReqDto {
        @NotEmpty //null이거나, 공백일 수 없다.
        private String username;
        @NotEmpty
        private String password;
        @NotEmpty
        private String email;
        @NotEmpty
        private String fullname;

        public User toEntity(PasswordEncoder passwordEncoder) {
            return User.builder()
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .email(email)
                    .fullname(fullname)
                    .role(UserEnum.CUSTOMER)
                    .build();
        }
    }
}
