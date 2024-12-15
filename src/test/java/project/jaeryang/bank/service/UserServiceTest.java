package project.jaeryang.bank.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import project.jaeryang.bank.config.dummy.DummyObject;
import project.jaeryang.bank.domain.user.UserRepository;
import project.jaeryang.bank.dto.user.UserReqDto.JoinReqDto;
import project.jaeryang.bank.dto.user.UserRespDto.JoinRespDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class) //가짜 환경이라 Spring 관련 Bean 들이 없는 환경
class UserServiceTest extends DummyObject {

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
        joinReqDto.setFullname("최재량");

        //stub (userRepository 는 믿고 쓴다)
        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.save(any())).thenReturn(newMockUser(1L, "cjl0701", "최재량"));

        //when
        JoinRespDto joinRespDto = userService.join(joinReqDto);

        //then
        assertEquals(joinReqDto.getUsername(), joinRespDto.getUsername());
        assertEquals(1L, joinRespDto.getId());
    }
}