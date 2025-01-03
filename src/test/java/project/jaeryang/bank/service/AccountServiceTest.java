package project.jaeryang.bank.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import project.jaeryang.bank.config.dummy.DummyObject;
import project.jaeryang.bank.domain.account.Account;
import project.jaeryang.bank.domain.account.AccountRepository;
import project.jaeryang.bank.domain.user.User;
import project.jaeryang.bank.domain.user.UserRepository;
import project.jaeryang.bank.dto.account.AccountReqDto.AccountSaveReqDto;
import project.jaeryang.bank.dto.account.AccountRespDto.AccountListRespDto.AccountDto;
import project.jaeryang.bank.dto.account.AccountRespDto.AccountSaveRespDto;
import project.jaeryang.bank.ex.CustomApiException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static project.jaeryang.bank.dto.account.AccountRespDto.AccountListRespDto;

@ExtendWith(MockitoExtension.class) // spring-boot-starter-test에서 자동으로 Mockito 추가해주는 Mock 프레임워크
class AccountServiceTest extends DummyObject {
    @InjectMocks //모든 Mock들이 주입된다
    private AccountService accountService;

    @Mock //진짜 객체와 비슷하게 동작하지만 프로그래머가 직접 그 객체의 행동을 관리하는 객체
    private AccountRepository accountRepository;
    @Mock
    private UserRepository userRepository;

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
        Assertions.assertThrows(CustomApiException.class, () -> accountService.계좌삭제(number, 2L));
    }
}