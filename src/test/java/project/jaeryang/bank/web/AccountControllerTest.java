package project.jaeryang.bank.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import project.jaeryang.bank.config.dummy.DummyObject;
import project.jaeryang.bank.domain.account.Account;
import project.jaeryang.bank.domain.account.AccountRepository;
import project.jaeryang.bank.domain.user.User;
import project.jaeryang.bank.domain.user.UserRepository;
import project.jaeryang.bank.dto.account.AccountReqDto.AccountSaveReqDto;
import project.jaeryang.bank.service.AccountService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class AccountControllerTest extends DummyObject {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    public void setUp() {
        userRepository.save(newUser("cjl0701", "최재량"));
    }

    //setupBefore=TEST_METHOD : setUp 메서드 실행 전에 수행됨
    //setupBefore = TestExecutionEvent.TEST_EXECUTION : 테스트 메서드 실행 전에 수행됨
    @WithUserDetails(value = "cjl0701", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    //DB에서 username으로 조회해서 세션에 담아준다.
    @Test
    public void saveAccount_test() throws Exception {
        //given
        AccountSaveReqDto accountSaveReqDto = new AccountSaveReqDto();
        accountSaveReqDto.setNumber(9999L);
        accountSaveReqDto.setPassword(1234L);
        String requestBody = objectMapper.writeValueAsString(accountSaveReqDto);

        //when & then
        mockMvc.perform(post("/api/s/account")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.number").value(9999L));
    }

    @WithUserDetails(value = "cjl0701", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void findUserAccounts_test() throws Exception {
        //given
        User user = userRepository.findByUsername("cjl0701").orElseThrow();
        Account account1 = newAccount(1111L, user);
        Account account2 = newAccount(2222L, user);
        accountRepository.save(account1);
        accountRepository.save(account2);

        //when & then
        mockMvc.perform(get("/api/s/account/login-user"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.fullname").value("최재량"))
                .andExpect(jsonPath("$.data.accounts.length()").value(2));
    }
}