package project.jaeryang.bank.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;
import project.jaeryang.bank.config.dummy.DummyObject;
import project.jaeryang.bank.domain.account.Account;
import project.jaeryang.bank.domain.account.AccountRepository;
import project.jaeryang.bank.domain.transaction.Transaction;
import project.jaeryang.bank.domain.transaction.TransactionRepository;
import project.jaeryang.bank.domain.user.User;
import project.jaeryang.bank.domain.user.UserRepository;
import project.jaeryang.bank.dto.account.AccountReqDto.AccountDepositReqDto;
import project.jaeryang.bank.dto.account.AccountReqDto.AccountSaveReqDto;
import project.jaeryang.bank.dto.account.AccountRespDto.AccountListRespDto.AccountDto;
import project.jaeryang.bank.dto.account.AccountRespDto.AccountSaveRespDto;
import project.jaeryang.bank.ex.CustomApiException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static project.jaeryang.bank.dto.account.AccountRespDto.AccountDepositRespDto;
import static project.jaeryang.bank.dto.account.AccountRespDto.AccountListRespDto;

@ExtendWith(MockitoExtension.class) // spring-boot-starter-test에서 자동으로 Mockito 추가해주는 Mock 프레임워크
class AccountServiceTest extends DummyObject {
    @InjectMocks //모든 Mock들이 주입된다
    private AccountService accountService;

    @Mock //진짜 객체와 비슷하게 동작하지만 프로그래머가 직접 그 객체의 행동을 관리하는 객체
    private AccountRepository accountRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TransactionRepository transactionRepository;

    @Spy
    private ObjectMapper objectMapper;

    @Test
    public void 계좌등록_test() throws Exception {
        //given
        Long userId = 1L;
        Long accountNumber = 1111L;

        AccountSaveReqDto accountSaveReqDto = new AccountSaveReqDto();
        accountSaveReqDto.setNumber(accountNumber);
        accountSaveReqDto.setPassword(1234L);

        //stub - DB를 검증할 것은 아니기 때문에 repository stubbing
        User mockUser = newMockUser(userId, "cjl0701", "최재량");
        when(userRepository.findById(any())).thenReturn(Optional.of(mockUser));

        when(accountRepository.findByNumber(any())).thenReturn(Optional.empty());

        when(accountRepository.save(any())).thenReturn(newMockAccount(1L, accountNumber, 1000L, mockUser));

        //when
        AccountSaveRespDto accountSaveRespDto = accountService.계좌등록(accountSaveReqDto, userId);
        System.out.println("accountSaveRespDto = " + objectMapper.writeValueAsString(accountSaveRespDto));
        ;

        //then
        assertThat(accountSaveRespDto.getNumber()).isEqualTo(accountNumber);
        assertThat(accountSaveRespDto.getBalance()).isEqualTo(1000L);
    }

    @Test
    public void 계좌목록보기_유저별_test() throws Exception {
        //given
        Long userId = 1L;
        User mockUser = newMockUser(userId, "cjl0701", "최재량");
        List<Account> mockAccounts = List.of(newMockAccount(1L, 1111L, 1000L, mockUser), newMockAccount(2L, 2222L, 1000L, mockUser));

        //stub
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(accountRepository.findByUserId(userId)).thenReturn(mockAccounts);

        //when
        AccountListRespDto accountListRespDto = accountService.계좌목록보기_유저별(userId);

        //then
        assertThat(accountListRespDto.getAccounts()).hasSize(2);
        assertThat(accountListRespDto.getFullname()).isEqualTo(mockUser.getFullname());
        assertThat(accountListRespDto.getAccounts().get(0)).isInstanceOf(AccountDto.class);
    }

    @Test
    public void 계좌삭제_test() throws Exception {
        //given
        Long number = 1111L;
        Long userId = 1L;
        User mockUser = newMockUser(userId, "cjl0701", "최재량");
        Account mockAccount = newMockAccount(1L, number, 1000L, mockUser);

        //stub
        when(accountRepository.findByNumber(number)).thenReturn(Optional.of(mockAccount));

        //when & then
        assertThrows(CustomApiException.class, () -> accountService.계좌삭제(number, 2L));
    }

    @Test
    public void 계좌입금_test() throws Exception {
        //given
        Long amount = 100L;
        Long balance = 1000L;
        AccountDepositReqDto accountDepositReqDto = new AccountDepositReqDto();
        accountDepositReqDto.setAmount(amount);
        accountDepositReqDto.setNumber(1111L);
        accountDepositReqDto.setTel("010-2758-8203");
        accountDepositReqDto.setTransactionType("DEPOSIT");

        //stub
        Account mockAccount = newMockAccount(1L, 1111L, balance, null);
        when(accountRepository.findByNumber(any())).thenReturn(Optional.of(mockAccount));
        when(transactionRepository.save(any())).thenAnswer((Answer<Transaction>) invocation -> {
            Transaction transaction = invocation.getArgument(0);
            //ReflectionTestUtils : 테스트 환경에서 리플렉션을 사용하여 객체의 비공개(private) 필드나 메서드에 접근하거나 수정할 때 사용
            ReflectionTestUtils.setField(transaction, "createdAt", LocalDateTime.now());
            return transaction;
        });

        //when
        AccountDepositRespDto accountDepositRespDto = accountService.계좌입금(accountDepositReqDto);

        //then
        assertThat(accountDepositRespDto.getTransactionDto().getSender()).isEqualTo("ATM");
        assertThat(accountDepositRespDto.getTransactionDto().getDepositAccountBalance()).isEqualTo(balance + amount);
    }

    /**
     * DTO 생성 여부는 컨트롤러 테스트에서 확인하고, 빠르게 기능이 잘 동작하는지만 테스트한다.
     * assertThrows는 테스트 환경에서만 로직을 실행하고, 애플리케이션의 실제 동작에는 영향을 미치지 않는다. (테스트 컨텍스트에서만 동작)
     */
    @Test
    public void 출금계좌_test() throws Exception {
        //given
        Long userId = 1L;
        Long password = 1234L;
        Long amount = 1000L;
        Long balance = 1000L;

        User mockUser = newMockUser(userId, "cjl0701", "최재량");
        Account withdrawAccount = newMockAccount(1L, password, balance, mockUser);

        //when
        //1. 0원 체크
        if (amount <= 0L)
            throw new CustomApiException("0원 이하의 금액을 출금할 수 없습니다.");


        //3. 본인 확인
        withdrawAccount.checkOwner(userId);
        assertThrows(CustomApiException.class, () -> withdrawAccount.checkOwner(2L));

        //4. 패스워드 확인
        withdrawAccount.checkPassword(password);
        assertThrows(CustomApiException.class, () -> withdrawAccount.checkPassword(1111L));

        //5. 출금계좌 잔액 확인
        withdrawAccount.checkBalance(amount);
        assertThrows(CustomApiException.class, () -> withdrawAccount.checkBalance(balance + 1000));

        //6. 출금 (잔액 확인이 누락되면 안되므로 출금 메소드에 녹인다!)
        withdrawAccount.withdraw(amount);
        assertThrows(CustomApiException.class, () -> withdrawAccount.withdraw(balance + 1000));

        //then
        assertThat(withdrawAccount.getBalance()).isEqualTo(balance - amount);
    }
}