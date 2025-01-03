package project.jaeryang.bank.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.jaeryang.bank.config.auth.LoginUser;
import project.jaeryang.bank.domain.account.Account;
import project.jaeryang.bank.domain.account.AccountRepository;
import project.jaeryang.bank.domain.user.User;
import project.jaeryang.bank.domain.user.UserRepository;
import project.jaeryang.bank.dto.account.AccountReqDto.AccountSaveReqDto;
import project.jaeryang.bank.dto.account.AccountRespDto.AccountListRespDto;
import project.jaeryang.bank.dto.account.AccountRespDto.AccountSaveRespDto;
import project.jaeryang.bank.ex.CustomApiException;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AccountService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public AccountSaveRespDto 계좌등록(AccountSaveReqDto accountSaveReqDto, Long userId) {
        // User 가 DB에 있는지 검증
        User userPS = userRepository.findById(userId)
                .orElseThrow(() -> new CustomApiException("존재하지 않는 회원 ID입니다."));

        // 해당 계좌 중복 여부 체크
        if (accountRepository.findByNumber(accountSaveReqDto.getNumber()).isPresent())
            throw new CustomApiException("해당 계좌가 이미 존재합니다.");

        // 계좌 등록
        Account accountPS = accountRepository.save(accountSaveReqDto.toEntity(userPS));

        //DTO 응답
        return new AccountSaveRespDto(accountPS);
    }

    public AccountListRespDto 계좌목록보기_유저별(Long userId) {
        User userPS = userRepository.findById(userId)
                .orElseThrow(() -> new CustomApiException("존재하지 않는 회원 ID입니다."));

        List<Account> accountPS = accountRepository.findByUserId(userId);
        return new AccountListRespDto(userPS, accountPS);
    }

    @Transactional
    public void 계좌삭제(Long accountNumber,Long userId) {
        // 1. 계좌 확인
        Account accountPS = accountRepository.findByNumber(accountNumber).orElseThrow(
                () -> new CustomApiException("존재하지 않는 계좌번호 입니다."));

        // 2. 계좌 소유자 확인
        accountPS.checkOwner(userId);

        // 3. 계좌 삭제
        accountRepository.delete(accountPS);
    }
}
