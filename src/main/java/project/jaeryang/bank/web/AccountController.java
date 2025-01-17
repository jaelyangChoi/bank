package project.jaeryang.bank.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import project.jaeryang.bank.config.auth.LoginUser;
import project.jaeryang.bank.dto.ResponseDto;
import project.jaeryang.bank.dto.account.AccountReqDto;
import project.jaeryang.bank.dto.account.AccountReqDto.AccountTransferReqDto;
import project.jaeryang.bank.dto.account.AccountReqDto.AccountWithdrawReqDto;
import project.jaeryang.bank.dto.account.AccountRespDto;
import project.jaeryang.bank.dto.account.AccountRespDto.AccountListRespDto;
import project.jaeryang.bank.dto.account.AccountRespDto.AccountSaveRespDto;
import project.jaeryang.bank.service.AccountService;

import static project.jaeryang.bank.dto.account.AccountReqDto.AccountDepositReqDto;
import static project.jaeryang.bank.dto.account.AccountReqDto.AccountSaveReqDto;
import static project.jaeryang.bank.dto.account.AccountRespDto.*;
import static project.jaeryang.bank.dto.account.AccountRespDto.AccountDepositRespDto;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/s/account")
    public ResponseEntity<ResponseDto<?>> createAccount(@RequestBody @Valid AccountSaveReqDto accountSaveReqDto,
                                                        BindingResult bindingResult,
                                                        @AuthenticationPrincipal LoginUser loginUser) {

        AccountSaveRespDto accountSaveRespDto = accountService.계좌등록(accountSaveReqDto, loginUser.getUser().getId());

        return new ResponseEntity<>(new ResponseDto<>(1, "계좌 생성 성공", accountSaveRespDto), HttpStatus.CREATED);
    }

    @GetMapping("/s/account/login-user")
    public ResponseEntity<ResponseDto<?>> findUserAccounts(@AuthenticationPrincipal LoginUser loginUser) {

        /* userId를 받아버리면 아래 검증 과정이 수반됨
         if (!Objects.equals(id, loginUser.getUser().getId()))
            throw new CustomForbiddenException("권한 오류: 본인의 계좌만 조회할 수 있습니다.");*/

        AccountListRespDto accountListRespDto = accountService.계좌목록보기_유저별(loginUser.getUser().getId());
        return new ResponseEntity<>(new ResponseDto<>(1, "계좌 목록 조회 성공", accountListRespDto), HttpStatus.OK);
    }

    @DeleteMapping("/s/account/{number}")
    public ResponseEntity<?> deleteAccount(@PathVariable("number") Long number, @AuthenticationPrincipal LoginUser loginUser) {
        accountService.계좌삭제(number, loginUser.getUser().getId());
        return new ResponseEntity<>(new ResponseDto<>(1, "계좌 삭제 완료", null), HttpStatus.OK);
    }

    @PostMapping("/account/deposit")
    public ResponseEntity<?> depositAccount(@RequestBody @Valid AccountDepositReqDto accountDepositReqDto, BindingResult bindingResult) {
        AccountDepositRespDto accountDepositRespDto = accountService.계좌입금(accountDepositReqDto);
        return new ResponseEntity<>(new ResponseDto<>(1, "계좌 입금 완료", accountDepositRespDto), HttpStatus.CREATED);
    }

    @PostMapping("/s/account/withdraw")
    public ResponseEntity<?> depositAccount(@RequestBody @Valid AccountWithdrawReqDto accountWithdrawReqDto,
                                            BindingResult bindingResult,
                                            @AuthenticationPrincipal LoginUser loginUser) {
        AccountWithdrawRespDto accountWithdrawRespDto = accountService.계좌출금(accountWithdrawReqDto, loginUser.getUser().getId());
        return new ResponseEntity<>(new ResponseDto<>(1, "계좌 출금 완료", accountWithdrawRespDto), HttpStatus.CREATED);
    }

    @PostMapping("/s/account/transfer")
    public ResponseEntity<?> transferAccount(@RequestBody @Valid AccountTransferReqDto accountTransferReqDto,
                                            BindingResult bindingResult,
                                            @AuthenticationPrincipal LoginUser loginUser) {
        AccountTransferRespDto accountTransferRespDto = accountService.계좌이체(accountTransferReqDto, loginUser.getUser().getId());
        return new ResponseEntity<>(new ResponseDto<>(1, "계좌 이체 완료", accountTransferRespDto), HttpStatus.CREATED);
    }
}
