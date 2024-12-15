package project.jaeryang.bank.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.jaeryang.bank.domain.user.User;
import project.jaeryang.bank.domain.user.UserEnum;
import project.jaeryang.bank.domain.user.UserRepository;
import project.jaeryang.bank.ex.CustomApiException;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public JoinRespDto join(JoinReqDto joinReqDto) {
        // 1. 동일 유저 네임 존재 검사
        if (userRepository.existsByUsername(joinReqDto.getUsername()))
            throw new CustomApiException("Username already exists");

        // 2. 패스워드 인코딩 + 회원가입
        User userPS = userRepository.save(joinReqDto.toEntity(passwordEncoder));

        // 3. dto 응답
        return new JoinRespDto(userPS);
    }

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

    @Getter
    @Setter
    public static class JoinRespDto {
        private Long id;
        private String username;
        private String fullname;

        public JoinRespDto(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.fullname = user.getFullname();
        }
    }

}
