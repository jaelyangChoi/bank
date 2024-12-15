package project.jaeryang.bank.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.jaeryang.bank.dto.ResponseDto;
import project.jaeryang.bank.dto.user.UserReqDto.JoinReqDto;
import project.jaeryang.bank.dto.user.UserRespDto.JoinRespDto;
import project.jaeryang.bank.service.UserService;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class UserController {

    private final UserService userService;

    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody @Valid JoinReqDto joinReqDto, BindingResult bindingResult) {
        JoinRespDto joinRespDto = userService.join(joinReqDto);
        return new ResponseEntity<>(new ResponseDto<>(1, "회원가입 성공", joinRespDto), HttpStatus.CREATED);
    }

}
