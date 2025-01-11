package project.jaeryang.bank.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.jaeryang.bank.domain.account.Account;
import project.jaeryang.bank.domain.account.AccountRepository;
import project.jaeryang.bank.domain.transaction.Transaction;
import project.jaeryang.bank.domain.transaction.TransactionEnum;
import project.jaeryang.bank.domain.transaction.TransactionRepository;
import project.jaeryang.bank.domain.user.User;
import project.jaeryang.bank.domain.user.UserRepository;
import project.jaeryang.bank.dto.account.AccountReqDto.AccountSaveReqDto;
import project.jaeryang.bank.dto.account.AccountReqDto.AccountWithdrawReqDto;
import project.jaeryang.bank.dto.account.AccountRespDto.AccountDepositRespDto;
import project.jaeryang.bank.dto.account.AccountRespDto.AccountListRespDto;
import project.jaeryang.bank.dto.account.AccountRespDto.AccountSaveRespDto;
import project.jaeryang.bank.ex.CustomApiException;

import java.util.List;

import static project.jaeryang.bank.dto.account.AccountReqDto.AccountDepositReqDto;
import static project.jaeryang.bank.dto.account.AccountRespDto.AccountWithdrawRespDto;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AccountService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

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
    public void 계좌삭제(Long accountNumber, Long userId) {
        // 1. 계좌 확인
        Account accountPS = accountRepository.findByNumber(accountNumber).orElseThrow(
                () -> new CustomApiException("존재하지 않는 계좌번호 입니다."));

        // 2. 계좌 소유자 확인
        accountPS.checkOwner(userId);

        // 3. 계좌 삭제
        accountRepository.delete(accountPS);
    }

    //ATM -> 계좌
    @Transactional
    public AccountDepositRespDto 계좌입금(AccountDepositReqDto accountDepositReqDto) {
        //1. 0원 체크
        if (accountDepositReqDto.getAmount() <= 0L)
            throw new CustomApiException("0원 이하의 금액을 입금할 수 없습니다.");

        //2. 입금 계좌 확인
        Account depositAccountPS = accountRepository.findByNumber(accountDepositReqDto.getNumber())
                .orElseThrow(() -> new CustomApiException("계좌를 찾을 수 없습니다."));

        //3. 입금
        depositAccountPS.deposit(accountDepositReqDto.getAmount());

        //4. 거래 내역
        Transaction transaction = Transaction.builder()
                .depositAccount(depositAccountPS)
                .withdrawAccount(null)
                .amount(accountDepositReqDto.getAmount())
                .depositAccountBalance(depositAccountPS.getBalance())
                .withdrawAccountBalance(null)
                .sender("ATM")
                .receiver(accountDepositReqDto.getNumber().toString())
                .tel(accountDepositReqDto.getTel())
                .transactionType(TransactionEnum.DEPOSIT)
                .build();
        Transaction transactionPS = transactionRepository.save(transaction);

        return new AccountDepositRespDto(depositAccountPS, transactionPS);
    }


    //ATM -> 계좌
    @Transactional
    public AccountWithdrawRespDto 계좌출금(AccountWithdrawReqDto accountWithdrawReqDto, Long userId) {
        //1. 0원 체크
        if (accountWithdrawReqDto.getAmount() <= 0L)
            throw new CustomApiException("0원 이하의 금액을 출금할 수 없습니다.");

        //2. 출금 계좌 확인
        Account withdrawAccountPS = accountRepository.findByNumber(accountWithdrawReqDto.getNumber())
                .orElseThrow(() -> new CustomApiException("계좌를 찾을 수 없습니다."));

        //3. 본인 확인
        withdrawAccountPS.checkOwner(userId);

        //4. 패스워드 확인
        withdrawAccountPS.checkPassword(accountWithdrawReqDto.getPassword());

        //5. 출금계좌 잔액 확인
        withdrawAccountPS.checkBalance(accountWithdrawReqDto.getAmount());

        //6. 출금
        withdrawAccountPS.withdraw(accountWithdrawReqDto.getAmount());

        //7. 거래 내역
        Transaction transaction = Transaction.builder()
                .withdrawAccount(withdrawAccountPS)
                .depositAccount(null)
                .withdrawAccountBalance(withdrawAccountPS.getBalance())
                .depositAccountBalance(null)
                .amount(accountWithdrawReqDto.getAmount())
                .sender(withdrawAccountPS.getNumber().toString())
                .receiver("ATM")
                .transactionType(TransactionEnum.WITHDRAW)
                .build();
        Transaction transactionPS = transactionRepository.save(transaction);

        return new AccountWithdrawRespDto(withdrawAccountPS, transactionPS);
    }
}
