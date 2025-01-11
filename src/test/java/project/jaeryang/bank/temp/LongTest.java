package project.jaeryang.bank.temp;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Fail.fail;

public class LongTest {

    @DisplayName("Long 타입은 == 비교할 때 2^8 범위의 수만 가능 => equals 사용하자!")
    @Test
    public void test() throws Exception {
        Long v1 = 100L;
        Long v2 = 100L;

        if (v1 == v2) {
            System.out.println("-127~128 사이의 값일 때 == 비교 정상 동작");
        } else {
            fail();
        }

        v1 = 1000L;
        v2 = 1000L;

        if (v1 == v2) {
            fail();
        } else {
            System.out.println("-127~128 이외 값일 때 == 비교 정상 동작 X");
        }

        if (v1.equals(v2)) {
            System.out.println("equals 비교는 정상 동작");
        } else {
            fail();
        }

    }
}
