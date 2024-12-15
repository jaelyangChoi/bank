package project.jaeryang.bank.temp;

import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RegexTest {

    @Test
    public void 한글만_된다() throws Exception {
        String value = "한글";
        boolean result = Pattern.matches("^[가-힣]+$", value);
        assertTrue(result);
    }

    @Test
    public void 한글은_안된다() throws Exception {
        String value = "!@#";
        boolean result = Pattern.matches("^[^ㄱ-ㅎ가-힣]*$", value);
        assertTrue(result);
    }

    @Test
    public void 영어만_된다() throws Exception {
        String value = "aaSDa";
        boolean result = Pattern.matches("^[a-zA-Z]+$", value);
        assertTrue(result);
    }

    @Test
    public void 영어는_안된다() throws Exception {
        String value = "한글";
        boolean result = Pattern.matches("^[^a-zA-Z]*$", value);
        assertTrue(result);
    }

    @Test
    public void 영어와숫자만_된다() throws Exception {
        String value = "A19123b";
        boolean result = Pattern.matches("^[a-zA-Z0-9]+$", value);
        assertTrue(result);
    }

    @Test
    public void 영어만_되고_길이는_최소2최대4이다() throws Exception {
        String value = "Adfd";
        boolean result = Pattern.matches("^[a-zA-Z]{2,4}$", value);
        assertTrue(result);
    }

    @Test
    public void user_username_test() throws Exception {
        String username = "cjl0701";
        boolean result = Pattern.matches("^[a-zA-Z0-9]{2,20}$", username);
        assertTrue(result);
    }

    @Test
    public void user_fullname_test() throws Exception {
        String fullname = "최재량";
        boolean result = Pattern.matches("^[a-zA-Z가-힣]{1,20}$", fullname);
        assertTrue(result);
    }

    @Test
    public void user_email_test() throws Exception {
        String email = "cjl2076@naver.com";
        boolean result = Pattern.matches("^[a-zA-Z0-9]{2,9}@[a-zA-Z]{2,6}\\.[a-zA-Z]{2,3}$", email);
        assertTrue(result);
    }
}
