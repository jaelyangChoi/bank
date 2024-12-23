package project.jaeryang.bank.config.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import project.jaeryang.bank.domain.user.User;
import project.jaeryang.bank.domain.user.UserRepository;

@Service
@RequiredArgsConstructor
public class LoginService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * loadUserByUsername 메서드의 역할은 사용자 정보를 가져오는 것이며, 패스워드 검증은 Spring Security의 인증 관리 계층에서 처리된다.
     * 패스워드 검증은 AuthenticationManager와 PasswordEncoder가 처리하며, 이를 통해 보안 관련 로직이 분리되고 더 안전하게 관리
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userPS = userRepository.findByUsername(username).orElseThrow(() -> new InternalAuthenticationServiceException("인증 실패"));
        return new LoginUser(userPS);
    }
}
