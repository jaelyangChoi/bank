package project.jaeryang.bank.config.jwt;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import project.jaeryang.bank.config.auth.LoginUser;
import project.jaeryang.bank.domain.user.User;
import project.jaeryang.bank.domain.user.UserEnum;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc //실제 HTTP 호출 없이 컨트롤러를 테스트
//컨텍스트 전체를 로드
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK) //MOCK (기본값) : 내장 Tomcat을 실행하지 않고, Mock 서블릿 환경에서 테스트를 수행
class JwtAuthorizationFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void authorization_success_test() throws Exception {
        //given
        User user = User.builder()
                .id(1L)
                .role(UserEnum.CUSTOMER)
                .build();
        LoginUser loginUser = new LoginUser(user);
        String jwtToken = JwtProcess.create(loginUser);
        System.out.println("jwtToken = " + jwtToken);

        //when & then
        mockMvc.perform(get("/api/s/hello/test").header(JwtVO.HEADER_STRING, jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    public void authorization_fail_test() throws Exception {
        //when & then
        mockMvc.perform(get("/api/s/hello/test"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void authorization_admin_test() throws Exception {
        //given
        User user = User.builder()
                .id(1L)
                .role(UserEnum.ADMIN)
                .build();
        LoginUser loginUser = new LoginUser(user);
        String jwtToken = JwtProcess.create(loginUser);
        System.out.println("jwtToken = " + jwtToken);

        //when & then
        mockMvc.perform(get("/api/admin/hello/test").header(JwtVO.HEADER_STRING, jwtToken))
                .andExpect(status().isNotFound());
    }
}