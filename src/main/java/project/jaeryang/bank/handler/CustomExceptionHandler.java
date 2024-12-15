package project.jaeryang.bank.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import project.jaeryang.bank.dto.ResponseDto;
import project.jaeryang.bank.ex.CustomApiException;

@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler {

    @ExceptionHandler(CustomApiException.class)
    public ResponseEntity<?> apiExceptionHandler(CustomApiException ex) {
        log.error(ex.getMessage());
        return new ResponseEntity<>(new ResponseDto<>(-1, ex.getMessage(), null), HttpStatus.BAD_REQUEST);
    }
}