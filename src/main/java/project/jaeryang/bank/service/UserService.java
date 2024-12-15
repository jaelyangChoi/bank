package project.jaeryang.bank.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.jaeryang.bank.domain.user.User;
import project.jaeryang.bank.domain.user.UserRepository;
import project.jaeryang.bank.dto.user.UserReqDto;
import project.jaeryang.bank.dto.user.UserReqDto.JoinReqDto;
import project.jaeryang.bank.dto.user.UserRespDto;
import project.jaeryang.bank.dto.user.UserRespDto.JoinRespDto;
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


}
