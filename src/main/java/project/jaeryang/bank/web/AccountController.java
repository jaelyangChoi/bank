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
import project.jaeryang.bank.dto.account.AccountRespDto.AccountListRespDto;
import project.jaeryang.bank.dto.account.AccountRespDto.AccountSaveRespDto;
import project.jaeryang.bank.service.AccountService;

import static project.jaeryang.bank.dto.account.AccountReqDto.AccountSaveReqDto;

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

    @DeleteMapping("/s/account/delete/{number}")
    public ResponseEntity<?> deleteAccount(@PathVariable("number") Long number, @AuthenticationPrincipal LoginUser loginUser) {
        accountService.계좌삭제(number, loginUser.getUser().getId());
        return new ResponseEntity<>(new ResponseDto<>(1, "계좌 삭제 성공", null), HttpStatus.OK);
    }
}
