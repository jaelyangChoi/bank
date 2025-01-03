package project.jaeryang.bank.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import project.jaeryang.bank.dto.ResponseDto;
import project.jaeryang.bank.ex.CustomApiException;
import project.jaeryang.bank.ex.CustomForbiddenException;
import project.jaeryang.bank.ex.CustomValidationException;

@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler {

    @ExceptionHandler(CustomApiException.class)
    public ResponseEntity<?> apiExceptionHandler(CustomApiException ex) {
        log.error(ex.getMessage());
        return new ResponseEntity<>(new ResponseDto<>(-1, ex.getMessage(), null), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomValidationException.class)
    public ResponseEntity<?> validationExceptionHandler(CustomValidationException ex) {
        log.error(ex.getMessage());
        return new ResponseEntity<>(new ResponseDto<>(-1, ex.getMessage(), ex.getErrorMap()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomForbiddenException.class)
    public ResponseEntity<?> forbiddenExceptionHandler(CustomForbiddenException ex) {
        log.error(ex.getMessage());
        return new ResponseEntity<>(new ResponseDto<>(-1, ex.getMessage(), null), HttpStatus.FORBIDDEN);
    }
}
