package project.jaeryang.bank.config.jwt;

import org.junit.jupiter.api.Test;
import project.jaeryang.bank.config.auth.LoginUser;
import project.jaeryang.bank.domain.user.User;
import project.jaeryang.bank.domain.user.UserEnum;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtProcessTest {

    @Test
    public void create_test() throws Exception {
        //given
        User user = User.builder()
                .id(1L)
                .role(UserEnum.CUSTOMER)
                .build();

        //when
        String jwtToken = JwtProcess.create(new LoginUser(user));

        //then
        assertTrue(jwtToken.startsWith(JwtVO.TOKEN_PREFIX));
    }

    @Test
    public void verify_test() throws Exception {
        //given
        User user = User.builder()
                .id(1L)
                .role(UserEnum.CUSTOMER)
                .build();

        String jwtToken = JwtProcess.create(new LoginUser(user));
        jwtToken = jwtToken.replace(JwtVO.TOKEN_PREFIX, "");

        //when
        LoginUser loginUser = JwtProcess.verify(jwtToken);

        //then
        assertThat(loginUser.getUser().getId()).isEqualTo(user.getId());
    }

}