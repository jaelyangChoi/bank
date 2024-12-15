package project.jaeryang.bank.dto.user;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;
import project.jaeryang.bank.domain.user.User;
import project.jaeryang.bank.domain.user.UserEnum;

public class UserReqDto {
    @Getter
    @Setter
    public static class JoinReqDto {
        // 유효성 검사
        private String username;
        private String password;
        private String email;
        private String fullName;

        public User toEntity(PasswordEncoder passwordEncoder) {
            return User.builder()
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .email(email)
                    .fullname(fullName)
                    .role(UserEnum.CUSTOMER)
                    .build();
        }
    }
}
