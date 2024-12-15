package project.jaeryang.bank.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import project.jaeryang.bank.domain.user.User;
import project.jaeryang.bank.domain.user.UserRepository;
import project.jaeryang.bank.service.UserService.JoinReqDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static project.jaeryang.bank.service.UserService.JoinRespDto;

@ExtendWith(MockitoExtension.class) //가짜 환경이라 Spring 관련 Bean들이 없는 환경
class UserServiceTest {

    @InjectMocks //@Mock 으로 생성한 객체를 주입해준다.
    private UserService userService;

    @Mock //가짜 객체를 생성해 넣는다
    private UserRepository userRepository;

    @Spy //Spring IOC 에 있는 진짜 Bean 을 넣는다
    private PasswordEncoder passwordEncoder;

    @Test
    public void 회원가입() throws Exception {
        //given
        JoinReqDto joinReqDto = new JoinReqDto();
        joinReqDto.setUsername("cjl0701");
        joinReqDto.setPassword("1234");
        joinReqDto.setEmail("cjl2076@naver.com");
        joinReqDto.setFullName("최재량");

        //stub (userRepository 는 믿고 쓴다)
        when(userRepository.existsByUsername(any())).thenReturn(false);
        User user = User.builder()
                .id(1L)
                .username(joinReqDto.getUsername())
                .password(joinReqDto.getPassword())
                .email(joinReqDto.getEmail())
                .fullname(joinReqDto.getFullName())
                .build();
        when(userRepository.save(any())).thenReturn(user);

        //when
        JoinRespDto joinRespDto = userService.join(joinReqDto);

        //then
        assertEquals(joinReqDto.getUsername(), joinRespDto.getUsername());
        assertEquals(1L, joinRespDto.getId());
    }
}