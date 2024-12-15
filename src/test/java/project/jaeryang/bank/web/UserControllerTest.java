package project.jaeryang.bank.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import project.jaeryang.bank.config.dummy.DummyObject;
import project.jaeryang.bank.domain.user.User;
import project.jaeryang.bank.domain.user.UserRepository;
import project.jaeryang.bank.dto.user.UserReqDto.JoinReqDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc //MockMvc를 자동으로 구성
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK) // Tomcat을 실행하지 않고, Mock 서블릿 환경에서 테스트를 수행
class UserControllerTest extends DummyObject {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void 회원가입_정상() throws Exception {
        //given
        JoinReqDto reqDto = new JoinReqDto();
        reqDto.setUsername("cjl0701");
        reqDto.setPassword("123456");
        reqDto.setEmail("cjl0701@gmail.com");
        reqDto.setFullname("최재량");

        String requestBody = objectMapper.writeValueAsString(reqDto);

        //when & then
        mockMvc.perform(post("/api/join")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("data.id").exists())
                .andExpect(jsonPath("data.username").value("cjl0701"))
        ;
    }

    @Test
    public void 회원가입_실패_중복회원() throws Exception {
        //given
        User user = newUser("cjl0701", "재량");
        userRepository.save(user);

        JoinReqDto reqDto = new JoinReqDto();
        reqDto.setUsername("cjl0701");
        reqDto.setPassword("123456");
        reqDto.setEmail("cjl0701@gmail.com");
        reqDto.setFullname("최재량");

        String requestBody = objectMapper.writeValueAsString(reqDto);

        //when & then
        mockMvc.perform(post("/api/join")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value("Username already exists"))
        ;
    }

    @Test
    public void 회원가입_실패_유효성검사() throws Exception {
        //given
        JoinReqDto reqDto = new JoinReqDto();
        reqDto.setUsername("한글은 안된다");
        reqDto.setPassword("1");
        reqDto.setEmail("cjl0701gmail.com");
        reqDto.setFullname("최재!");

        String requestBody = objectMapper.writeValueAsString(reqDto);

        //when & then
        mockMvc.perform(post("/api/join")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("data.username").value("영문/숫자 2~20자 이내로 작성해주세요"))
                .andExpect(jsonPath("data.password").value("size must be between 4 and 20"))
                .andExpect(jsonPath("data.email").value("must be a well-formed email address"))
                .andExpect(jsonPath("data.fullname").value("영문/한글 1~20자 이내로 작성해주세요"))

        ;
    }
}