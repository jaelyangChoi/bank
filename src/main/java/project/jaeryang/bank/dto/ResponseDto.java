package project.jaeryang.bank.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ResponseDto<T> {
    private final Integer code; //1 성공, -1 실패
    private final String message;
    private final T data;
}