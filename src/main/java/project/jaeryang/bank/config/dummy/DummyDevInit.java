package project.jaeryang.bank.config.dummy;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import project.jaeryang.bank.domain.account.AccountRepository;
import project.jaeryang.bank.domain.user.User;
import project.jaeryang.bank.domain.user.UserRepository;

@Configuration
public class DummyDevInit extends DummyObject {

    @Profile("dev")
    @Bean
    CommandLineRunner init(UserRepository userRepository, AccountRepository accountRepository) {
        return args -> {
            User cjl0701 = userRepository.save(newUser("cjl0701", "최재량"));
            User testUser = userRepository.save(newUser("test", "테스트계정"));
            accountRepository.save(newAccount(1111L, cjl0701));
            accountRepository.save(newAccount(2222L, cjl0701));
            accountRepository.save(newAccount(3333L, testUser));
        };
    }
}
