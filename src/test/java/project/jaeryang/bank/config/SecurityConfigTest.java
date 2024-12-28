package project.jaeryang.bank.config;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@AutoConfigureMockMvc //Mock(가짜) 환경에 MockMvc가 등록됨
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void authentication_test() throws Exception {
        //given

        //when
        ResultActions resultActions = mockMvc.perform(get("/api/s/hello"));
        int httpStatusCode = resultActions.andReturn().getResponse().getStatus();
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("httpStatusCode = " + httpStatusCode);
        System.out.println("responseBody = " + responseBody);

        //then
        assertThat(httpStatusCode).isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    public void authorization_test() throws Exception {
        //given

        //when
        ResultActions resultActions = mockMvc.perform(get("/api/admin/hello"));
        int httpStatusCode = resultActions.andReturn().getResponse().getStatus();
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("httpStatusCode = " + httpStatusCode);
        System.out.println("responseBody = " + responseBody);

        //then
        assertThat(httpStatusCode).isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
