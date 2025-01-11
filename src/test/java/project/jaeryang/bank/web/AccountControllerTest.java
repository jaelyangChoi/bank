package project.jaeryang.bank.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import project.jaeryang.bank.config.dummy.DummyObject;
import project.jaeryang.bank.domain.account.AccountRepository;
import project.jaeryang.bank.domain.user.User;
import project.jaeryang.bank.domain.user.UserRepository;
import project.jaeryang.bank.dto.account.AccountReqDto;
import project.jaeryang.bank.dto.account.AccountReqDto.AccountDepositReqDto;
import project.jaeryang.bank.dto.account.AccountReqDto.AccountSaveReqDto;
import project.jaeryang.bank.dto.account.AccountReqDto.AccountWithdrawReqDto;
import project.jaeryang.bank.dto.account.AccountRespDto;
import project.jaeryang.bank.dto.account.AccountRespDto.AccountDepositRespDto;
import project.jaeryang.bank.ex.CustomApiException;
import project.jaeryang.bank.service.AccountService;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test") //dev 모드에서 초기화 데이터를 넣으므로 분리
//@Transactional
@Sql("classpath:db/teardown.sql") //PK 초기화를 위해 @BeforeEach 실행마다 테이블 초기화
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
    private AccountRepository accountRepository;

    @Autowired
    private EntityManager em;
    @Autowired
    private AccountService accountService;

    private final Long testAccountNumber = 1111L;
    private final Long testAccountPassword = 1234L;
    private final Long testAccountBalance = 1000L;

    @BeforeEach
    public void setUp() {
        User cjl0701 = userRepository.save(newUser("cjl0701", "최재량"));
        User testUser = userRepository.save(newUser("test", "테스트계정"));
        accountRepository.save(newAccount(testAccountNumber, cjl0701));
        accountRepository.save(newAccount(2222L, cjl0701));
        accountRepository.save(newAccount(3333L, testUser));
        em.clear(); //쿼리 확인을 위해 영속성 컨텍스트 비우기
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
        //when & then
        mockMvc.perform(get("/api/s/account/login-user"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.fullname").value("최재량"))
                .andExpect(jsonPath("$.data.accounts.length()").value(2));
    }

    //User 정보 조회할 때 영속성컨텍스트에 User 정보가 저장된다. 정확한 쿼리를 보기 위해선 em.clear로 초기화 해주자.
    @WithUserDetails(value = "cjl0701", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void deleteAccount_test() throws Exception {
        em.clear();
        //given
        Long number = 1111L;

        //when & then
        mockMvc.perform(delete("/api/s/account/" + number))
                .andDo(print())
                .andExpect(status().isOk());

        // JUnit 테스트에서 delete 쿼리는 가장 마지막에 오면 발동 안됨.
        assertThrows(CustomApiException.class, () -> accountRepository.findByNumber(number)
                .orElseThrow(() -> new CustomApiException("계좌를 찾을 수 없습니다.")));
    }

    @Test
    public void depositAccount_test() throws Exception {
        //given
        AccountDepositReqDto accountDepositReqDto = new AccountDepositReqDto();
        accountDepositReqDto.setAmount(100L);
        accountDepositReqDto.setNumber(testAccountNumber);
        accountDepositReqDto.setTel("01027588203");
        accountDepositReqDto.setTransactionType("DEPOSIT");

        //when & then
        mockMvc.perform(post("/api/account/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountDepositReqDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.transactionDto.sender").value("ATM"));

    }

    @Test
    @WithUserDetails(value = "cjl0701", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void withdrawAccount_test() throws Exception {
        //given
        Long amount = 100L;
        AccountWithdrawReqDto accountWithdrawReqDto = new AccountWithdrawReqDto();
        accountWithdrawReqDto.setNumber(testAccountNumber);
        accountWithdrawReqDto.setPassword(testAccountPassword);
        accountWithdrawReqDto.setAmount(amount);
        accountWithdrawReqDto.setTransactionType("WITHDRAW");

        //when & then
        mockMvc.perform(post("/api/s/account/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountWithdrawReqDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.balance").value(Long.toString(testAccountBalance - amount)));
    }
}