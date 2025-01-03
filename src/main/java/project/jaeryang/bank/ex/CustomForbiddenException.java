package project.jaeryang.bank.ex;

import lombok.Getter;

@Getter
public class CustomForbiddenException extends RuntimeException {
    public CustomForbiddenException(String message) {
        super(message);
    }
}
