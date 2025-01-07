package project.jaeryang.bank.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
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
import project.jaeryang.bank.dto.account.AccountRespDto.AccountListRespDto;
import project.jaeryang.bank.dto.account.AccountRespDto.AccountSaveRespDto;
import project.jaeryang.bank.ex.CustomApiException;
import project.jaeryang.bank.util.CustomDateUtil;

import java.util.List;

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

        //2.입금 계좌 확인
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

    @Getter
    @Setter
    public static class AccountDepositReqDto {
        @NotNull
        @Digits(integer = 4, fraction = 4)
        private Long number;
        @NotNull
        private Long amount;
        @NotEmpty
        @Pattern(regexp = "^(DEPOSIT)$")
        private String transactionType;
        @NotEmpty
        @Pattern(regexp = "^[0-9]{3}-[0-9]{4}-[0-9]{4}")
        private String tel;
    }

    @Getter
    @Setter
    public static class AccountDepositRespDto {
        private Long id;
        private Long number;
        private TransactionDto transactionDto;

        public AccountDepositRespDto(Account account, Transaction transaction) {
            this.id = account.getId();
            this.number = account.getNumber();
            this.transactionDto = new TransactionDto(transaction);
        }

        @Getter
        @Setter
        public static class TransactionDto {
            private Long id;
            private String transactionType;
            private String sender;
            private String receiver;
            private Long amount;
            private String tel;
            private String createdAt;
            @JsonIgnore
            private Long depositAccountBalance; //클라이언트에게 전달x, 서비스단에서 테스트 용도

            public TransactionDto(Transaction transaction) {
                this.id = transaction.getId();
                this.transactionType = transaction.getTransactionType().getValue();
                this.sender = transaction.getSender();
                this.receiver = transaction.getReceiver();
                this.amount = transaction.getAmount();
                this.tel = transaction.getTel();
                this.createdAt = CustomDateUtil.toStringFormat(transaction.getCreatedAt());
                this.depositAccountBalance = transaction.getDepositAccountBalance();
            }
        }
    }
}
