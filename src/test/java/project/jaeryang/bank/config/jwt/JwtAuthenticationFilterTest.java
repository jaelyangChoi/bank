package project.jaeryang.bank.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import project.jaeryang.bank.config.dummy.DummyObject;
import project.jaeryang.bank.domain.user.UserRepository;
import project.jaeryang.bank.dto.user.UserReqDto;
import project.jaeryang.bank.dto.user.UserRespDto;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc //실제 HTTP 호출 없이 컨트롤러를 테스트
//컨텍스트 전체를 로드
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK) //MOCK (기본값) : 내장 Tomcat을 실행하지 않고, Mock 서블릿 환경에서 테스트를 수행
class JwtAuthenticationFilterTest extends DummyObject {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.save(newUser("cjl0701", "최재량"));
    }

    @Test
    public void successfulAuthentication_test() throws Exception {
        //given
        String username = "cjl0701";
        UserReqDto.LoginReqDto loginReqDto = new UserReqDto.LoginReqDto();
        loginReqDto.setUsername(username);
        loginReqDto.setPassword("1234");
        String requestBody = objectMapper.writeValueAsString(loginReqDto);
        System.out.println("requestBody = " + requestBody);

        //when
        ResultActions resultActions = mockMvc.perform(post("/api/login")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON));

        String jwtToken = resultActions.andReturn().getResponse().getHeader(JwtVO.HEADER_STRING);
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("jwtToken = " + jwtToken);
        System.out.println("responseBody = " + responseBody);

        //then
        resultActions.andExpect(status().isOk());
        assertNotNull(jwtToken);
        assertTrue(jwtToken.startsWith(JwtVO.TOKEN_PREFIX));
        resultActions.andExpect(jsonPath("$.data.username").value(username)); //$가 최상위
    }

    @Test
    public void unSuccessfulAuthentication_test() throws Exception {
        //given

        //when

        //then
    }
}