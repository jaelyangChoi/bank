package project.jaeryang.bank.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import project.jaeryang.bank.dto.ResponseDto;

import java.io.IOException;

@Slf4j
public class CustomResponseUtil {

    public static void success(HttpServletResponse response, Object dto) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ResponseDto<?> responseDto = new ResponseDto<>(1, "로그인 성공", dto);
            String responseBody = objectMapper.writeValueAsString(responseDto);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println(responseBody);
        } catch (IOException e) {
            log.error("서버 파싱 에러: {}", e.getMessage());
        }
    }

    public static void fail(HttpServletResponse response, String message, HttpStatus httpStatus) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ResponseDto<?> responseDto = new ResponseDto<>(-1, message, null);
            String responseBody = objectMapper.writeValueAsString(responseDto);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(httpStatus.value());
            response.getWriter().println(responseBody);
        } catch (IOException e) {
            log.error("서버 파싱 에러: {}", e.getMessage());
        }
    }
}
